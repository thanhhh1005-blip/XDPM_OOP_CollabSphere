package com.collab.collaborationservice.service.impl;

import com.collab.collaborationservice.dto.request.AddMemberRequest;
import com.collab.collaborationservice.dto.response.MemberResponse;
import com.collab.collaborationservice.entity.Collaboration;
import com.collab.collaborationservice.entity.CollaborationMember;
import com.collab.collaborationservice.enums.CollaborationRole;
import com.collab.collaborationservice.repository.CollaborationMemberRepository;
import com.collab.collaborationservice.repository.CollaborationRepository;
import com.collab.collaborationservice.service.CollaborationMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CollaborationMemberServiceImpl implements CollaborationMemberService {

    private final CollaborationRepository collaborationRepository;
    private final CollaborationMemberRepository memberRepository;

    // ===================== ADD MEMBER =====================
    @Override
    public void addMember(Long collaborationId,
                          AddMemberRequest request,
                          Long requesterId) {

        Collaboration collaboration = getCollaboration(collaborationId);

        CollaborationMember requester = getMember(collaborationId, requesterId);

        // Chỉ OWNER hoặc ADMIN được thêm member
        if (requester.getRole() != CollaborationRole.OWNER &&
            requester.getRole() != CollaborationRole.ADMIN) {
            throw new RuntimeException("No permission to add member");
        }

        // Không add trùng
        if (memberRepository.existsByCollaborationIdAndUserId(
                collaborationId, request.getUserId())) {
            throw new RuntimeException("User already in collaboration");
        }

        CollaborationMember member = CollaborationMember.builder()
                .collaboration(collaboration)
                .userId(request.getUserId())
                .role(request.getRole())
                .active(true)
                .build();

        memberRepository.save(member);
    }

    // ===================== REMOVE MEMBER =====================
    @Override
    public void removeMember(Long collaborationId,
                             Long memberId,
                             Long requesterId) {

        CollaborationMember requester = getMember(collaborationId, requesterId);

        // Chỉ OWNER được remove
        if (requester.getRole() != CollaborationRole.OWNER) {
            throw new RuntimeException("Only OWNER can remove member");
        }

        CollaborationMember member = getMember(collaborationId, memberId);

        if (member.getRole() == CollaborationRole.OWNER) {
            throw new RuntimeException("Cannot remove OWNER");
        }

        member.setActive(false);
        memberRepository.save(member);
    }

    // ===================== LIST MEMBERS =====================
    @Override
    public List<MemberResponse> listMembers(Long collaborationId) {

        return memberRepository.findByCollaborationId(collaborationId)
                .stream()
                .map(member -> MemberResponse.builder()
                        .userId(member.getUserId())
                        .role(member.getRole())
                        .active(member.isActive())
                        .build())
                .toList();
    }

    // ===================== VALIDATE ROLE =====================
    @Override
    public void validateMemberRole(Long collaborationId,
                                   Long userId,
                                   CollaborationRole requiredRole) {

        CollaborationMember member = getMember(collaborationId, userId);

        if (member.getRole() != requiredRole) {
            throw new RuntimeException(
                    "Required role: " + requiredRole +
                    ", but found: " + member.getRole()
            );
        }
    }

    // ===================== HELPERS =====================
    private Collaboration getCollaboration(Long collaborationId) {
        return collaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new RuntimeException("Collaboration not found"));
    }

    private CollaborationMember getMember(Long collaborationId, Long userId) {
        return memberRepository
                .findByCollaborationIdAndUserId(collaborationId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a collaboration member"));
    }

    @Override
    public void addMember(Long collaborationId, String userId, String role, AddMemberRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addMember'");
    }
}
