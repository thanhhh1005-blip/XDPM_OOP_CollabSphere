package com.collab.collaborationservice.repository;

import com.collab.collaborationservice.entity.CollaborationActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaborationActivityRepository
        extends JpaRepository<CollaborationActivity, Long> {

    // Lấy activity theo collaboration (timeline)
    List<CollaborationActivity> findByCollaborationIdOrderByCreatedAtDesc(
            Long collaborationId
    );
}
