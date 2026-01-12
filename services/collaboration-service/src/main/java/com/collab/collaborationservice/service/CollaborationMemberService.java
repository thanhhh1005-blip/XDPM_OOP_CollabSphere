package com.collab.collaborationservice.service;

import com.collab.collaborationservice.dto.request.AddMemberRequest;
import com.collab.collaborationservice.dto.response.MemberResponse;
import com.collab.collaborationservice.enums.CollaborationRole;

import java.util.List;

public interface CollaborationMemberService {

    void addMember(Long collaborationId, AddMemberRequest request, Long requesterId);

    void removeMember(Long collaborationId, Long memberId, Long requesterId);

    List<MemberResponse> listMembers(Long collaborationId);

    void validateMemberRole(Long collaborationId, Long userId, CollaborationRole requiredRole);

    void addMember(Long collaborationId, String userId, String role, AddMemberRequest request);
}
