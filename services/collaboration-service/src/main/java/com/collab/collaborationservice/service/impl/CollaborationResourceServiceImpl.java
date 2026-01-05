package com.collab.collaborationservice.service.impl;

import com.collab.collaborationservice.dto.request.ShareResourceRequest;
import com.collab.collaborationservice.dto.response.ResourceResponse;
import com.collab.collaborationservice.entity.Collaboration;
import com.collab.collaborationservice.entity.CollaborationMember;
import com.collab.collaborationservice.entity.CollaborationResource;
import com.collab.collaborationservice.enums.CollaborationRole;
import com.collab.collaborationservice.repository.CollaborationMemberRepository;
import com.collab.collaborationservice.repository.CollaborationRepository;
import com.collab.collaborationservice.repository.CollaborationResourceRepository;
import com.collab.collaborationservice.service.CollaborationResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CollaborationResourceServiceImpl implements CollaborationResourceService {

    private final CollaborationRepository collaborationRepository;
    private final CollaborationMemberRepository memberRepository;
    private final CollaborationResourceRepository resourceRepository;

    // ===================== SHARE RESOURCE =====================
    @Override
    public void shareResource(Long collaborationId,
                              ShareResourceRequest request,
                              Long requesterId) {

        // 1. Validate collaboration tồn tại
        Collaboration collaboration = collaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new RuntimeException("Collaboration not found"));

        // 2. Validate quyền share
        validateSharePermission(collaborationId, requesterId);

        // 3. Không cho share trùng resource
        if (resourceRepository.existsByCollaborationIdAndResourceId(
                collaborationId, request.getResourceId())) {
            throw new RuntimeException("Resource already shared in this collaboration");
        }

        // 4. Lưu resource
        CollaborationResource resource = CollaborationResource.builder()
                .collaboration(collaboration)
                .resourceId(request.getResourceId())
                .sharedBy(requesterId)
                .build();

        resourceRepository.save(resource);
    }

    // ===================== LIST SHARED RESOURCES =====================
    @Override
    public List<ResourceResponse> listSharedResources(Long collaborationId) {

        // Check collaboration tồn tại
        if (!collaborationRepository.existsById(collaborationId)) {
            throw new RuntimeException("Collaboration not found");
        }

        return resourceRepository.findByCollaborationId(collaborationId)
                .stream()
                .map(r -> ResourceResponse.builder()
                        .resourceId(r.getResourceId())
                        .sharedBy(r.getSharedBy())
                        .sharedAt(r.getCreatedAt())
                        .build())
                .toList();
    }

    // ===================== VALIDATE PERMISSION =====================
    @Override
    public void validateSharePermission(Long collaborationId, Long userId) {

        CollaborationMember member = memberRepository
                .findByCollaborationIdAndUserId(collaborationId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a collaboration member"));

        // VIEWER không được share
        if (member.getRole() == CollaborationRole.VIEWER) {
            throw new RuntimeException("Viewer is not allowed to share resources");
        }
    }
}
