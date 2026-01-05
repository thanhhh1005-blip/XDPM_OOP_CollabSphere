package com.collab.collaborationservice.controller;

import com.collab.collaborationservice.dto.request.ShareResourceRequest;
import com.collab.collaborationservice.dto.response.ApiResponse;
import com.collab.collaborationservice.dto.response.ResourceResponse;
import com.collab.collaborationservice.service.CollaborationResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborations/{collaborationId}/resources")
@RequiredArgsConstructor
public class CollaborationResourceController {

    private final CollaborationResourceService resourceService;

    // Chia sẻ resource
    @PostMapping
    public ApiResponse<Void> shareResource(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role,
            @PathVariable Long collaborationId,
            @RequestBody ShareResourceRequest request
    ) {
        resourceService.shareResource(collaborationId, userId, role, request);
        return ApiResponse.success();
    }

    // Danh sách resource
    @GetMapping
    public ApiResponse<List<ResourceResponse>> listResources(
            @PathVariable Long collaborationId
    ) {
        return ApiResponse.success(
                resourceService.listResources(collaborationId)
        );
    }
}
