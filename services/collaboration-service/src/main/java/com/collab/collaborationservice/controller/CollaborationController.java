package com.collab.collaborationservice.controller;

import com.collab.collaborationservice.dto.request.CreateCollaborationRequest;
import com.collab.collaborationservice.dto.response.CollaborationResponse;
import com.collab.collaborationservice.dto.response.ApiResponse;
import com.collab.collaborationservice.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborations")
@RequiredArgsConstructor
public class CollaborationController {

    private final CollaborationService collaborationService;

    // Tạo collaboration
    @PostMapping
    public ApiResponse<CollaborationResponse> create(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role,
            @RequestBody CreateCollaborationRequest request
    ) {
        return ApiResponse.success(
                collaborationService.createCollaboration(userId, role, request)
        );
    }

    // Lấy chi tiết collaboration
    @GetMapping("/{id}")
    public ApiResponse<CollaborationResponse> getDetail(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                collaborationService.getCollaborationDetail(id, userId)
        );
    }

    // Danh sách collaboration của user
    @GetMapping
    public ApiResponse<List<CollaborationResponse>> getByUser(
            @RequestHeader("X-USER-ID") String userId
    ) {
        return ApiResponse.success(
                collaborationService.getCollaborationsByUser(userId)
        );
    }

    // Đóng collaboration
    @PutMapping("/{id}/close")
    public ApiResponse<Void> close(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role,
            @PathVariable Long id
    ) {
        collaborationService.closeCollaboration(id, userId, role);
        return ApiResponse.success();
    }
}
