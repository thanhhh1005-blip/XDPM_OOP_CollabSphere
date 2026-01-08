package com.collab.resourceservice.service.impl;

import com.collab.resourceservice.entity.Resource;
import com.collab.resourceservice.enums.ResourceType;
import com.collab.resourceservice.enums.UserRole; // Đã sửa từ Role -> UserRole
import com.collab.resourceservice.exception.BadRequestException;
import com.collab.resourceservice.exception.FileStorageException;
import com.collab.resourceservice.exception.ForbiddenException;
import com.collab.resourceservice.exception.ResourceNotFoundException; // Đã sửa tên
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
    private static final String UPLOAD_DIR = "uploads";

    @Override
    public Resource upload(MultipartFile file, String uploadedBy, String uploaderRole) {
        UserRole role = parseRole(uploaderRole);
        validateUploadPermission(role);

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new FileStorageException("Cannot create upload directory");
        }

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir, storedFileName);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new FileStorageException("Upload file failed");
        }

        Resource resource = Resource.builder()
                .fileName(file.getOriginalFilename())
                .storedFileName(storedFileName)
                .filePath(dest.getAbsolutePath())
                .fileSize(file.getSize())
                .type(detectType(file.getOriginalFilename()))
                .uploadedBy(uploadedBy)
                .uploaderRole(role.name())
                .deleted(false)
                .build();

        return resourceRepository.save(resource);
    }

    @Override
    public List<Resource> getAll() {
        return resourceRepository.findByDeletedFalse();
    }
    
    @Override
    public Resource getById(Long id) {
        return resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
    }

    @Override
    public void delete(Long id, String requesterRole) {
        UserRole role = parseRole(requesterRole);
        validateDeletePermission(role);

        Resource resource = getById(id);
        resource.setDeleted(true);
        resourceRepository.save(resource);
    }

    private void validateUploadPermission(UserRole role) {
        if (role == UserRole.USER) { // Ví dụ logic, tùy em chỉnh
            throw new ForbiddenException("USER is not allowed to upload resource");
        }
    }

    private void validateDeletePermission(UserRole role) {
        if (role != UserRole.ADMIN && role != UserRole.HEAD_DEPARTMENT) {
            throw new ForbiddenException("You do not have permission to delete resource");
        }
    }

    private UserRole parseRole(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid role: " + role);
        }
    }

    private ResourceType detectType(String fileName) {
        if (fileName == null) return ResourceType.OTHER;
        String name = fileName.toLowerCase();
        if (name.endsWith(".pdf")) return ResourceType.PDF;
        if (name.endsWith(".doc") || name.endsWith(".docx")) return ResourceType.DOCX;
        if (name.endsWith(".mp4")) return ResourceType.VIDEO;
        return ResourceType.OTHER;
    }
}