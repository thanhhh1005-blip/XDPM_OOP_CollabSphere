package com.collab.resourceservice.controller;

import com.collab.resourceservice.dto.ApiResponse;
import com.collab.resourceservice.dto.ResourceResponse;
import com.collab.resourceservice.enums.ResourceScope;
import com.collab.resourceservice.enums.UserRole;
import com.collab.resourceservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    // 1. API UPLOAD FILE
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResourceResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("scope") ResourceScope scope,
            @RequestParam("scopeId") String scopeId,
            @RequestParam("uploaderId") String uploaderId,
            @RequestParam("role") UserRole role
    ) {
        try {
            ResourceResponse response = resourceService.uploadFile(file, scope, scopeId, uploaderId, role);
            return ResponseEntity.ok(ApiResponse.success("Upload successful", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi Server: " + e.getMessage()));
        }
    }

    // 2. API DOWNLOAD FILE
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") Long id) {
        try {
            ResourceResponse resourceInfo = resourceService.getResourceById(id);
            byte[] data = resourceService.downloadFile(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(resourceInfo.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceInfo.getFileName() + "\"")
                    .body(data);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 3. API LẤY DANH SÁCH
    @GetMapping
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getResources(
            @RequestParam("scope") ResourceScope scope,
            @RequestParam("scopeId") String scopeId
    ) {
        List<ResourceResponse> resources = resourceService.getResourcesByScope(scope, scopeId);
        return ResponseEntity.ok(ApiResponse.success("Success", resources));
    }

    // 4. API XÓA FILE (DELETE) ---> ĐÂY LÀ PHẦN QUAN TRỌNG
    // URL: DELETE http://localhost:8084/api/resources/{id}?userId=...
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResource(
            @PathVariable("id") Long id,
            @RequestParam("userId") String userId,
            @RequestParam("role") UserRole role
    ) {
        try {
            resourceService.deleteResource(id, userId, role);
            return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
        } catch (RuntimeException e) {
            // Lỗi do không có quyền (403 Forbidden)
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}