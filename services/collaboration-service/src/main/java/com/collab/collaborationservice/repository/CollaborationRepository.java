package com.collab.collaborationservice.repository;

import com.collab.collaborationservice.entity.Collaboration;
import com.collab.collaborationservice.enums.CollaborationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaborationRepository extends JpaRepository<Collaboration, Long> {

    // Lấy các collaboration còn hoạt động
    List<Collaboration> findByStatus(CollaborationStatus status);

    // Lấy collaboration do user tạo
    List<Collaboration> findByCreatedBy(String createdBy);
}
