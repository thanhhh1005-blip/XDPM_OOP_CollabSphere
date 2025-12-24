package com.collab.resourceservice.service.impl;

import com.collab.resourceservice.entity.Resource;
import com.collab.resourceservice.enums.ResourceType;
import com.collab.resourceservice.enums.Role;
import com.collab.resourceservice.repository.ResourceRepository;
import com.collab.resourceservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    // Thư mục lưu file local
    private static final String UPLOAD_DIR = "uploads";

    // ===================== UPLOAD =====================
    @Override
    public Resource upload(MultipartFile file, String uploadedBy, String uploaderRole) {

        validateUploadPermission(uploaderRole);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Tạo thư mục nếu chưa tồn tại
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Tạo tên file lưu
        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir, storedFileName);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("Upload file failed", e);
        }

        Resource resource = Resource.builder()
                .fileName(file.getOriginalFilename())
                .storedFileName(storedFileName)
                .filePath(dest.getAbsolutePath())
                .fileSize(file.getSize())
                .type(detectType(file.getOriginalFilename()))
                .uploadedBy(uploadedBy)
                .uploaderRole(uploaderRole)
                .deleted(false)
                .build();

        return resourceRepository.save(resource);
    }

    // ===================== LIST =====================
    @Override
    public List<Resource> getAll() {
        return resourceRepository.findByDeletedFalse();
    }

    // ===================== DELETE =====================
    @Override
    public void delete(Long id, String requesterRole) {

        validateDeletePermission(requesterRole);

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        resource.setDeleted(true);
        resourceRepository.save(resource);
    }

    // ===================== ROLE VALIDATION =====================
    private void validateUploadPermission(String role) {
        Role userRole = Role.valueOf(role.toUpperCase());

        if (userRole == Role.USER) {
            throw new RuntimeException("USER is not allowed to upload resource");
        }
    }

    private void validateDeletePermission(String role) {
        Role userRole = Role.valueOf(role.toUpperCase());

        if (userRole != Role.ADMIN && userRole != Role.HEAD_DEPARTMENT) {
            throw new RuntimeException("You do not have permission to delete resource");
        }
    }

    // ===================== FILE TYPE =====================
    private ResourceType detectType(String fileName) {

        if (fileName == null) {
            return ResourceType.OTHER;
        }

        String name = fileName.toLowerCase();

        if (name.endsWith(".pdf")) return ResourceType.PDF;
        if (name.endsWith(".doc") || name.endsWith(".docx")) return ResourceType.DOCX;
        if (name.endsWith(".mp4")) return ResourceType.VIDEO;

        return ResourceType.OTHER;
    }
}
