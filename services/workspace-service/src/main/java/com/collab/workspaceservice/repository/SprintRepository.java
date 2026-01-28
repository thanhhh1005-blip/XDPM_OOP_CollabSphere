package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProjectId(Long projectId);
    List<Sprint> findByMilestoneId(Long milestoneId);
    List<Sprint> findByWorkspaceId(Long workspaceId);
    List<Sprint> findByWorkspaceIdAndClassId(Long workspaceId, Long classId);

    List<Sprint> findByWorkspaceIdAndTeamId(Long workspaceId, String teamId);
}