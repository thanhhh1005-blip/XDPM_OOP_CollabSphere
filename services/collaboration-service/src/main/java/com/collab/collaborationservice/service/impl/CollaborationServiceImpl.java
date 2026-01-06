package com.collab.collaborationservice.service.impl;

import com.collab.collaborationservice.dto.request.CreateCollaborationRequest;
import com.collab.collaborationservice.dto.response.CollaborationResponse;
import com.collab.collaborationservice.dto.response.MemberResponse;
import com.collab.collaborationservice.entity.Collaboration;
import com.collab.collaborationservice.entity.CollaborationMember;
import com.collab.collaborationservice.enums.CollaborationRole;
import com.collab.collaborationservice.enums.CollaborationStatus;
import com.collab.collaborationservice.repository.CollaborationMemberRepository;
import com.collab.collaborationservice.repository.CollaborationRepository;
import com.collab.collaborationservice.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CollaborationServiceImpl implements CollaborationService {

    private final CollaborationRepository collaborationRepository;
    private final CollaborationMemberRepository memberRepository;

    // ===================== CREATE =====================
    @Override
    public CollaborationResponse create(CreateCollaborationRequest request) {

        Collaboration collaboration = Collaboration.builder()
                .name(request.getName())
                .description(request.getDescription())
                .teamId(request.getTeamId())
                .createdBy(request.getCreatedBy())
                .status(CollaborationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        Collaboration saved = collaborationRepository.save(collaboration);

        // Người tạo là OWNER
        CollaborationMember owner = CollaborationMember.builder()
                .collaboration(saved)
                .userId(request.getCreatedBy())
                .role(CollaborationRole.OWNER)
                .active(true)
                .build();

        memberRepository.save(owner);

        return mapToResponse(saved);
    }

    // ===================== DETAIL =====================
    @Override
    @Transactional(readOnly = true)
    public CollaborationResponse getDetail(Long collaborationId) {

        Collaboration collaboration = collaborationRepository.findById(collaborationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Collaboration not found: " + collaborationId));

        return mapToResponse(collaboration);
    }

    // ===================== LIST BY USER =====================
    @Override
    @Transactional(readOnly = true)
    public List<CollaborationResponse> getByUser(Long userId) {

        return collaborationRepository.findByMemberUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ===================== CLOSE =====================
    @Override
    public void close(Long collaborationId, Long requesterId) {

        Collaboration collaboration = collaborationRepository.findById(collaborationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Collaboration not found: " + collaborationId));

        CollaborationMember member = memberRepository
                .findByCollaborationIdAndUserId(collaborationId, requesterId)
                .orElseThrow(() ->
                        new IllegalStateException("User is not a collaboration member"));

        if (member.getRole() != CollaborationRole.OWNER) {
            throw new IllegalStateException("Only OWNER can close collaboration");
        }

        collaboration.setStatus(CollaborationStatus.CLOSED);
        collaborationRepository.save(collaboration);
    }

    // ===================== MAPPER =====================
    private CollaborationResponse mapToResponse(Collaboration collaboration) {

        List<MemberResponse> members = memberRepository
                .findByCollaborationId(collaboration.getId())
                .stream()
                .map(member -> MemberResponse.builder()
                        .userId(member.getUserId())
                        .role(member.getRole())
                        .active(member.isActive())
                        .build())
                .toList();

        return CollaborationResponse.builder()
                .id(collaboration.getId())
                .name(collaboration.getName())
                .description(collaboration.getDescription())
                .status(collaboration.getStatus())
                .teamId(collaboration.getTeamId())
                .createdBy(collaboration.getCreatedBy())
                .createdAt(collaboration.getCreatedAt())
                .members(members)
                .build();
    }
}
