package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
    List<SubTask> findByMilestoneIdAndTeamId(Long milestoneId, String teamId);
    List<SubTask> findByTeamId(String teamId);
}