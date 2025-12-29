package com.collab.collaborationservice.service;

public interface CollaborationResourceService {

    void shareResource(
            Long collaborationId,
            Long resourceId,
            String sharedBy
    );
}
