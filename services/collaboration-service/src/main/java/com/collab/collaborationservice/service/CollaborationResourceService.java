package com.collab.collaborationservice.service;

import com.collab.collaborationservice.dto.request.ShareResourceRequest;
import com.collab.collaborationservice.dto.response.ResourceResponse;

import java.util.List;

public interface CollaborationResourceService {

    void shareResource(Long collaborationId,
                       ShareResourceRequest request,
                       Long requesterId);

    List<ResourceResponse> listSharedResources(Long collaborationId);

    void validateSharePermission(Long collaborationId, Long userId);
}
