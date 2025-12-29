package com.collab.collaborationservice.service;

import com.collab.collaborationservice.entity.Collaboration;

import java.util.List;

public interface CollaborationService {

    Collaboration createCollaboration(
            String name,
            String description,
            String createdBy
    );

    List<Collaboration> getMyCollaborations(String userId);

    Collaboration getById(Long collaborationId);
}
