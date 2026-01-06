package com.collab.collaborationservice.service;

import com.collab.collaborationservice.dto.request.CreateCollaborationRequest;
import com.collab.collaborationservice.dto.response.CollaborationResponse;

import java.util.List;

public interface CollaborationService {

    CollaborationResponse create(CreateCollaborationRequest request);

    CollaborationResponse getDetail(Long collaborationId);

    List<CollaborationResponse> getByUser(Long userId);

    void close(Long collaborationId, Long requesterId);
}
