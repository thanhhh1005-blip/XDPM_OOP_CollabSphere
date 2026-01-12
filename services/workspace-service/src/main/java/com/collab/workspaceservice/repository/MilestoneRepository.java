package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Milestone; // Dòng này sẽ hết lỗi
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
}