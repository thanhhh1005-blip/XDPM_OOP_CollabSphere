package com.collab.collaborationservice.controller;

import com.collab.collaborationservice.dto.request.CreateCollaborationRequest;
import com.collab.collaborationservice.dto.response.CollaborationResponse;
import com.collab.collaborationservice.service.CollaborationService;
import com.collab.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborations")
@RequiredArgsConstructor
public class CollaborationController {

    private final CollaborationService collaborationService;

    // 1. Tạo Collaboration
    @PostMapping
    public ApiResponse<CollaborationResponse> create(
            @RequestHeader("X-USER-ID") String userId, // Header luôn là String
            @RequestBody CreateCollaborationRequest request
    ) {
        // Chuyển String sang Long và gán vào request
        request.setCreatedBy(Long.parseLong(userId));
        
        // Gọi hàm create bên Service (Chỉ nhận request)
        CollaborationResponse response = collaborationService.create(request);
        
        // Trả về ApiResponse đúng cú pháp (new ApiResponse...)
        return new ApiResponse<>(true, "Create success", response);
    }

    // 2. Lấy chi tiết
    @GetMapping("/{id}")
    public ApiResponse<CollaborationResponse> getDetail(@PathVariable Long id) {
        // Gọi hàm getDetail bên Service
        return new ApiResponse<>(true, "Success", collaborationService.getDetail(id));
    }

    // 3. Lấy danh sách theo User
    @GetMapping
    public ApiResponse<List<CollaborationResponse>> getByUser(
            @RequestHeader("X-USER-ID") String userId
    ) {
        // Chuyển String userId sang Long
        return new ApiResponse<>(true, "Success", collaborationService.getByUser(Long.parseLong(userId)));
    }

    // 4. Đóng Collaboration
    @PutMapping("/{id}/close")
    public ApiResponse<Void> close(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable Long id
    ) {
        // Gọi hàm close bên Service (tham số: id, requesterId)
        collaborationService.close(id, Long.parseLong(userId));
        return new ApiResponse<>(true, "Collaboration closed", null);
    }
}