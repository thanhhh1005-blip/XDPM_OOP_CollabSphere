package com.collab.collaborationservice.repository;

import com.collab.collaborationservice.entity.CollaborationMember;
import com.collab.collaborationservice.enums.CollaborationRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollaborationMemberRepository
        extends JpaRepository<CollaborationMember, Long> {

    // Lấy danh sách thành viên của collaboration
        List<CollaborationMember> findByCollaborationId(Long collaborationId);

    // Kiểm tra user có thuộc collaboration không
        Optional<CollaborationMember> findByCollaborationIdAndUserId(
        Long collaborationId,
        String userId
        );

    // Lấy user theo role
        List<CollaborationMember> findByCollaborationIdAndRole(
                Long collaborationId,
                CollaborationRole role
        );

        boolean existsByCollaborationIdAndUserId(Long collaborationId, Long userId);

        Optional<CollaborationMember> findByCollaborationIdAndUserId(Long collaborationId, Long userId);
}
