package com.collab.resourceservice.service.impl;

import com.collab.resourceservice.dto.ResourceResponse;
import com.collab.resourceservice.entity.Resource;
import com.collab.resourceservice.enums.ResourceScope;
import com.collab.resourceservice.enums.ResourceType;
import com.collab.resourceservice.enums.UserRole;
import com.collab.resourceservice.repository.ResourceRepository;
import com.collab.resourceservice.service.ResourceService;
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

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.endpoint}")
    private String endpointUrl;

    // 1. UPLOAD FILE
    @Override
    public ResourceResponse uploadFile(MultipartFile file, ResourceScope scope, String scopeId, String uploaderId, UserRole uploaderRole) {
        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        // Tạo tên file duy nhất (UUID)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        // Upload lên MinIO
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

        // Lưu DB
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

    // 2. DOWNLOAD FILE (Mới thêm logic)
    @Override
    public byte[] downloadFile(Long resourceId) {
        // Tìm file trong DB để lấy tên storedFileName (UUID)
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + resourceId));

        try {
            // Gọi MinIO để lấy nội dung file
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(resource.getStoredFileName())
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray(); // Trả về mảng byte dữ liệu

        } catch (Exception e) {
            log.error("Error downloading from S3", e);
            throw new RuntimeException("Could not download file from storage");
        }
    }

    // 3. GET INFO (Mới thêm logic)
    @Override
    public ResourceResponse getResourceById(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + resourceId));
        return mapToResponse(resource);
    }

    // 4. GET LIST (ĐÃ CẬP NHẬT LOGIC)
    @Override
    public List<ResourceResponse> getResourcesByScope(ResourceScope scope, String scopeId) {
        // 1. Gọi Repo lấy danh sách Entity
        List<Resource> resources = resourceRepository.findByScopeAndScopeIdOrderByCreatedAtDesc(scope, scopeId);

        // 2. Chuyển đổi từ Entity -> DTO (ResourceResponse) để trả về
        return resources.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 5. DELETE FILE (ĐÃ CẬP NHẬT LOGIC)
    @Override
    public void deleteResource(Long resourceId, String userId, UserRole role) {
        // 1. Tìm file trong DB
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // 2. Kiểm tra quyền xóa (Chỉ người up file hoặc GV/Admin mới được xóa)
        // Nếu không phải người up VÀ không phải GV/Admin -> Báo lỗi
        if (!resource.getUploadedBy().equals(userId) && 
            role != UserRole.LECTURER && 
            role != UserRole.ADMIN) {
            throw new RuntimeException("You do not have permission to delete this file");
        }

        // 3. Xóa trên MinIO
        try {
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(resource.getStoredFileName()));
        } catch (Exception e) {
            log.error("Error deleting from S3", e);
            // Vẫn tiếp tục xóa trong DB để tránh rác dữ liệu
        }

        // 4. Xóa trong Database
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