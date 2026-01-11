package com.collab.resourceservice.controller;

<<<<<<< HEAD
=======
import com.collab.resourceservice.dto.ApiResponse;
>>>>>>> origin/main
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
<<<<<<< HEAD
=======
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
>>>>>>> origin/main
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

<<<<<<< HEAD
    // ===================== UPLOAD =====================
    @PostMapping("/upload")
    public ResponseEntity<Resource> upload(
=======
    /* ===================== UPLOAD ===================== */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Resource>> upload(
>>>>>>> origin/main
            @RequestParam("file") MultipartFile file,
            @RequestParam String uploadedBy,
            @RequestParam String uploaderRole
    ) {
        Resource resource = resourceService.upload(file, uploadedBy, uploaderRole);
<<<<<<< HEAD
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
=======
        return ResponseEntity.ok(ApiResponse.success("Upload thành công", resource));
    }

    /* ===================== LIST ===================== */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Resource>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getAll()));
    }

    /* ===================== GET BY ID ===================== */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Resource>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getById(id)));
    }

    /* ===================== DOWNLOAD ===================== */
>>>>>>> origin/main
    @GetMapping("/download/{id}")
    public ResponseEntity<FileSystemResource> download(@PathVariable Long id) {

        Resource resource = resourceService.getById(id);
        File file = new File(resource.getFilePath());

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

<<<<<<< HEAD
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFileName() + "\""
=======
        String encodedFileName =
                URLEncoder.encode(resource.getFileName(), StandardCharsets.UTF_8)
                        .replace("+", "%20");

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFileName
>>>>>>> origin/main
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(new FileSystemResource(file));
    }

<<<<<<< HEAD
    // ===================== DELETE (SOFT DELETE) =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
=======
    /* ===================== DELETE (SOFT DELETE) ===================== */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
>>>>>>> origin/main
            @PathVariable Long id,
            @RequestParam String requesterRole
    ) {
        resourceService.delete(id, requesterRole);
<<<<<<< HEAD
        return ResponseEntity.ok("Deleted successfully");
    }
}
=======
        return ResponseEntity.ok(ApiResponse.success("Xóa thành công", null));
    }
}
>>>>>>> origin/main
