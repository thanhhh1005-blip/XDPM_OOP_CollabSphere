package com.collab.resourceservice.service.impl;

import com.collab.resourceservice.dto.ResourceResponse;
import com.collab.resourceservice.entity.Resource;
import com.collab.resourceservice.enums.ResourceScope;
import com.collab.resourceservice.enums.ResourceType;
import com.collab.resourceservice.enums.UserRole;
import com.collab.resourceservice.repository.ResourceRepository;
import com.collab.resourceservice.service.ResourceService;
import com.collab.resourceservice.service.NifiClient; // 👈 Import NifiClient (Sửa package nếu cần)
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final S3Client s3Client;
    private final ResourceRepository resourceRepository;
    
    // 👇 INJECT NIFI CLIENT (MỚI THÊM)
    private final NifiClient nifiClient;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.endpoint}")
    private String endpointUrl;

    // =========================================================================
    // 1. UPLOAD FILE THƯỜNG (LÊN MINIO/S3) - GIỮ NGUYÊN
    // =========================================================================
    @Override
    public ResourceResponse uploadFile(MultipartFile file, ResourceScope scope, String scopeId, String uploaderId, UserRole uploaderRole) {
        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putOb, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("Uploaded file to MinIO: {}", storedFileName);
        } catch (IOException e) {
            log.error("Error uploading to S3", e);
            throw new RuntimeException("Failed to upload file to storage");
        }

        String fileUrl = endpointUrl + "/" + bucketName + "/" + storedFileName;
        Resource resource = Resource.builder()
                .fileName(originalFilename)
                .storedFileName(storedFileName)
                .filePath(fileUrl)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .type(determineResourceType(file.getContentType()))
                .scope(scope)
                .scopeId(scopeId)
                .uploadedBy(uploaderId)
                .uploaderRole(uploaderRole)
                .build();

        Resource savedResource = resourceRepository.save(resource);
        return mapToResponse(savedResource);
    }
    
    // =========================================================================
    // ✨ [MỚI] GỬI FILE QUA NIFI ĐỂ XỬ LÝ (IMPORT DATA)
    // =========================================================================
    // (Lưu ý: Bạn cần thêm hàm này vào Interface ResourceService nữa nhé)
    public void importDataViaNifi(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File rỗng!");
        
        // Gửi sang NiFi Client, bắn vào endpoint "resources"
        nifiClient.sendFile(file, "resources");
        
        log.info("Đã chuyển file Resource sang NiFi để xử lý!");
    }

    // =========================================================================
    // 2. DOWNLOAD FILE
    // =========================================================================
    @Override
    public byte[] downloadFile(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + resourceId));

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(resource.getStoredFileName())
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();

        } catch (Exception e) {
            log.error("Error downloading from S3", e);
            throw new RuntimeException("Could not download file from storage");
        }
    }

    // =========================================================================
    // 3. CÁC HÀM GET & DELETE (GIỮ NGUYÊN)
    // =========================================================================
    @Override
    public ResourceResponse getResourceById(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + resourceId));
        return mapToResponse(resource);
    }

    @Override
    public List<ResourceResponse> getResourcesByScope(ResourceScope scope, String scopeId) {
        List<Resource> resources = resourceRepository.findByScopeAndScopeIdOrderByCreatedAtDesc(scope, scopeId);
        return resources.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteResource(Long resourceId, String userId, UserRole role) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!resource.getUploadedBy().equals(userId) && role != UserRole.LECTURER && role != UserRole.ADMIN) {
            throw new RuntimeException("You do not have permission to delete this file");
        }

        try {
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(resource.getStoredFileName()));
        } catch (Exception e) {
            log.error("Error deleting from S3", e);
        }

        resourceRepository.delete(resource);
    }

    // --- Helper Methods ---

    private ResourceResponse mapToResponse(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .fileName(resource.getFileName())
                .fileUrl(resource.getFilePath())
                .contentType(resource.getContentType())
                .fileSize(resource.getFileSize())
                .type(resource.getType())
                .scope(resource.getScope())
                .scopeId(resource.getScopeId())
                .uploadedBy(resource.getUploadedBy())
                .uploaderRole(resource.getUploaderRole())
                .createdAt(resource.getCreatedAt())
                .build();
    }

    private ResourceType determineResourceType(String contentType) {
        if (contentType == null) return ResourceType.OTHER;
        if (contentType.contains("pdf")) return ResourceType.PDF;
        if (contentType.contains("image")) return ResourceType.IMAGE;
        if (contentType.contains("video")) return ResourceType.VIDEO;
        if (contentType.contains("word") || contentType.contains("document")) return ResourceType.DOCX;
        return ResourceType.OTHER;
    }
}