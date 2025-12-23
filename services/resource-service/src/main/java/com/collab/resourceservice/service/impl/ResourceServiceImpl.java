package com.collab.resourceservice.service.impl;

import com.collab.resourceservice.entity.Resource;
import com.collab.resourceservice.enums.ResourceType;
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

    @Override
    public Resource upload(MultipartFile file, String uploadedBy, String uploaderRole) {

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

    @Override
    public List<Resource> getAll() {
        return resourceRepository.findByDeletedFalse();
    }

    @Override
    public void delete(Long id, String requesterRole) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        // Chỉ ADMIN mới được xoá (tạm thời đơn giản)
        if (!"ADMIN".equalsIgnoreCase(requesterRole)) {
            throw new RuntimeException("No permission to delete resource");
        }

        resource.setDeleted(true);
        resourceRepository.save(resource);
    }

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
