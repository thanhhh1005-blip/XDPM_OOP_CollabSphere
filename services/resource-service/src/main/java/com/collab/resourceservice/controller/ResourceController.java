package com.collab.resourceservice.controller;

import com.collab.resourceservice.entity.Resource;
import com.collab.resourceservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    // ===================== UPLOAD =====================
    @PostMapping("/upload")
    public ResponseEntity<Resource> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam String uploadedBy,
            @RequestParam String uploaderRole
    ) {
        Resource resource = resourceService.upload(file, uploadedBy, uploaderRole);
        return ResponseEntity.ok(resource);
    }

    // ===================== LIST =====================
    @GetMapping
    public ResponseEntity<List<Resource>> getAll() {
        return ResponseEntity.ok(resourceService.getAll());
    }

    // ===================== GET BY ID (optional nhưng nên có) =====================
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.getById(id));
    }

    // ===================== DOWNLOAD =====================
    @GetMapping("/download/{id}")
    public ResponseEntity<FileSystemResource> download(@PathVariable Long id) {

        Resource resource = resourceService.getById(id);
        File file = new File(resource.getFilePath());

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFileName() + "\""
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(new FileSystemResource(file));
    }

    // ===================== DELETE (SOFT DELETE) =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @RequestParam String requesterRole
    ) {
        resourceService.delete(id, requesterRole);
        return ResponseEntity.ok("Deleted successfully");
    }
}