package com.collab.collaborationservice.service;

public interface CollaborationActivityService {

    void log(
            Long collaborationId,
            String action,
            String actor,
            String description
    );
}
