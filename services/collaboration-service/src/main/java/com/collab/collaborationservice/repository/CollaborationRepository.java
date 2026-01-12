package com.collab.collaborationservice.repository;

import com.collab.collaborationservice.entity.Collaboration;
import com.collab.collaborationservice.entity.CollaborationMember;
import com.collab.collaborationservice.enums.CollaborationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CollaborationRepository extends JpaRepository<Collaboration, Long> {

    // Lấy các collaboration còn hoạt động
    List<Collaboration> findByStatus(CollaborationStatus status);

    // Lấy collaboration do user tạo
    List<Collaboration> findByCreatedBy(String createdBy);
    Optional<CollaborationMember> findByCollaborationIdAndUserId(Long collaborationId, Long userId);

    @Query("SELECT c FROM Collaboration c JOIN CollaborationMember m ON c.id = m.collaboration.id WHERE m.userId = :userId")
    List<Collaboration> findByMemberUserId(@Param("userId") Long userId);
}
