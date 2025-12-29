package com.collab.collaborationservice.service.impl;

import com.collab.collaborationservice.entity.Collaboration;
import com.collab.collaborationservice.entity.CollaborationMember;
import com.collab.collaborationservice.enums.CollaborationRole;
import com.collab.collaborationservice.repository.CollaborationMemberRepository;
import com.collab.collaborationservice.repository.CollaborationRepository;
import com.collab.collaborationservice.service.CollaborationActivityService;
import com.collab.collaborationservice.service.CollaborationMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollaborationMemberServiceImpl implements CollaborationMemberService {

    private final CollaborationRepository collaborationRepository;
    private final CollaborationMemberRepository memberRepository;
    private final CollaborationActivityService activityService;

    @Override
    public void addMember(
            Long collaborationId,
            String requesterId,
            String userId,
            CollaborationRole role
    ) {

        validateOwner(collaborationId, requesterId);

        Collaboration collaboration = collaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new RuntimeException("Collaboration not found"));

        if (memberRepository.findByCollaborationIdAndUserId(collaborationId, userId).isPresent()) {
            throw new RuntimeException("User already in collaboration");
        }

        memberRepository.save(
                CollaborationMember.builder()
                        .collaboration(collaboration)
                        .userId(userId)
                        .role(role)
                        .build()
        );

        activityService.log(
                collaborationId,
                "ADD_MEMBER",
                requesterId,
                "Added member " + userId
        );
    }

    @Override
    public void removeMember(
            Long collaborationId,
            String requesterId,
            String userId
    ) {

        validateOwner(collaborationId, requesterId);

        CollaborationMember member = memberRepository
                .findByCollaborationIdAndUserId(collaborationId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        memberRepository.delete(member);

        activityService.log(
                collaborationId,
                "REMOVE_MEMBER",
                requesterId,
                "Removed member " + userId
        );
    }

    @Override
    public boolean isMember(Long collaborationId, String userId) {
        return memberRepository
                .findByCollaborationIdAndUserId(collaborationId, userId)
                .isPresent();
    }

    private void validateOwner(Long collaborationId, String requesterId) {
        CollaborationMember requester = memberRepository
                .findByCollaborationIdAndUserId(collaborationId, requesterId)
                .orElseThrow(() -> new RuntimeException("Access denied"));

        if (requester.getRole() != CollaborationRole.OWNER) {
            throw new RuntimeException("Only OWNER can manage members");
        }
    }
}
