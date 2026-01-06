package com.collab.collaborationservice.service;

import com.collab.collaborationservice.dto.response.ActivityResponse;

import java.util.List;

public interface CollaborationActivityService {

    void logActivity(Long collaborationId,
                     String action,
                     Long actorId,
                     String description);

    List<ActivityResponse> getActivityHistory(Long collaborationId);
}
