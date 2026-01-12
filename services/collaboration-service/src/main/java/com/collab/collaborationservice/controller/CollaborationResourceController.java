package com.collab.collaborationservice.controller;

import com.collab.collaborationservice.dto.request.ShareResourceRequest;
import com.collab.collaborationservice.dto.response.ResourceResponse;
import com.collab.collaborationservice.service.CollaborationResourceService;
import com.collab.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborations/{collaborationId}/resources")
@RequiredArgsConstructor
public class CollaborationResourceController {

    private final CollaborationResourceService resourceService;

    // Chia sẻ Resource
    @PostMapping
    public ApiResponse<Void> shareResource(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable Long collaborationId,
            @RequestBody ShareResourceRequest request
    ) {
        // Gọi hàm shareResource bên Service (tham số: collaborationId, request, requesterId)
        resourceService.shareResource(collaborationId, request, Long.parseLong(userId));
        return new ApiResponse<>(true, "Resource shared successfully", null);
    }

    // Danh sách Resource
    @GetMapping
    public ApiResponse<List<ResourceResponse>> listResources(@PathVariable Long collaborationId) {
        return new ApiResponse<>(true, "Success", resourceService.listSharedResources(collaborationId));
    }
}