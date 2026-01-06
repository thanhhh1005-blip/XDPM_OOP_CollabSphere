package com.collab.collaborationservice.repository;

import com.collab.collaborationservice.entity.CollaborationResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaborationResourceRepository
        extends JpaRepository<CollaborationResource, Long> {

    // Lấy resource theo collaboration
    List<CollaborationResource> findByCollaborationId(Long collaborationId);

    // Kiểm tra resource đã được share chưa
    boolean existsByCollaborationIdAndResourceId(
            Long collaborationId,
            Long resourceId
    );
}
