package com.collab.resourceservice.service.impl;

import com.collab.resourceservice.entity.Resource;
import com.collab.resourceservice.enums.ResourceType;
import com.collab.resourceservice.enums.Role;
import com.collab.resourceservice.exception.BadRequestException;
import com.collab.resourceservice.exception.FileStorageException;
import com.collab.resourceservice.exception.ForbiddenException;
import com.collab.resourceservice.exception.NotFoundException;
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

    // ===================== UPLOAD =====================
    @Override
    public Resource upload(MultipartFile file, String uploadedBy, String uploaderRole) {

        Role role = parseRole(uploaderRole);
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

    // ===================== LIST =====================
    @Override
    public List<Resource> getAll() {
        return resourceRepository.findByDeletedFalse();
    }

    // ===================== DELETE =====================
    @Override
    public void delete(Long id, String requesterRole) {

        Role role = parseRole(requesterRole);
        validateDeletePermission(role);

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resource not found with id: " + id));

        resource.setDeleted(true);
        resourceRepository.save(resource);
    }

    // ===================== ROLE VALIDATION =====================
    private void validateUploadPermission(Role role) {
        if (role == Role.USER) {
            throw new ForbiddenException("USER is not allowed to upload resource");
        }
    }

    private void validateDeletePermission(Role role) {
        if (role != Role.ADMIN && role != Role.HEAD_DEPARTMENT) {
            throw new ForbiddenException("You do not have permission to delete resource");
        }
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Invalid role: " + role);
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
