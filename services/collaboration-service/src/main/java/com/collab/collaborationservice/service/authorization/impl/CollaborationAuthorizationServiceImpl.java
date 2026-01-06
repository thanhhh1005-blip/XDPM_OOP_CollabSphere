package com.collab.collaborationservice.service.authorization.impl;

import com.collab.collaborationservice.entity.CollaborationMember;
import com.collab.collaborationservice.enums.CollaborationRole;
import com.collab.collaborationservice.repository.CollaborationMemberRepository;
import com.collab.collaborationservice.service.authorization.CollaborationAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollaborationAuthorizationServiceImpl
        implements CollaborationAuthorizationService {

    private final CollaborationMemberRepository memberRepository;

    // ===================== MEMBER VALIDATION =====================
    @Override
    public CollaborationRole validateMember(Long collaborationId, Long userId) {

        CollaborationMember member = memberRepository
                .findByCollaborationIdAndUserId(collaborationId, userId)
                .orElseThrow(() ->
                        new RuntimeException("User is not a member of this collaboration"));

        return member.getRole();
    }

    // ===================== LEADER ONLY =====================
    @Override
    public void validateLeader(Long collaborationId, Long userId) {

        CollaborationRole role = validateMember(collaborationId, userId);

        if (role != CollaborationRole.LEADER
                && role != CollaborationRole.LECTURER) {
            throw new RuntimeException("Only leader can perform this action");
        }
    }

    // ===================== SHARE PERMISSION =====================
    @Override
    public void validateSharePermission(Long collaborationId, Long userId) {

        CollaborationRole role = validateMember(collaborationId, userId);

        if (role == CollaborationRole.VIEWER) {
            throw new RuntimeException("Viewer is not allowed to share resource");
        }
    }

    // ===================== VIEW PERMISSION =====================
    @Override
    public void validateViewPermission(Long collaborationId, Long userId) {

        // Chỉ cần là member
        validateMember(collaborationId, userId);
    }
}
