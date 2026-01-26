package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> findByMilestoneId(Long milestoneId);
}