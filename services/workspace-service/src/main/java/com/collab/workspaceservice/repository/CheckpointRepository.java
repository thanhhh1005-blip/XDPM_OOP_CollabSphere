package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Checkpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {
    
    Checkpoint findByMilestoneIdAndTeamId(Long milestoneId, String teamId);

    List<Checkpoint> findByMilestoneId(Long milestoneId);

    List<Checkpoint> findByTeamId(String teamId);

    @Query("SELECT c.milestoneId, COUNT(c) FROM Checkpoint c WHERE c.milestoneId IN :milestoneIds GROUP BY c.milestoneId")
    List<Object[]> countSubmissionsByMilestoneIds(@Param("milestoneIds") List<Long> milestoneIds);
}
