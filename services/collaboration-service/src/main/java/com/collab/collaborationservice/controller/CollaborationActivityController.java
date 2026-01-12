package com.collab.collaborationservice.controller;

import com.collab.collaborationservice.dto.response.ActivityResponse;
import com.collab.collaborationservice.service.CollaborationActivityService;
import com.collab.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborations/{collaborationId}/activities")
@RequiredArgsConstructor
public class CollaborationActivityController {

    private final CollaborationActivityService activityService;

    @GetMapping
    public ApiResponse<List<ActivityResponse>> getActivityHistory(@PathVariable Long collaborationId) {
        return new ApiResponse<>(true, "Success", activityService.getActivityHistory(collaborationId));
    }
}