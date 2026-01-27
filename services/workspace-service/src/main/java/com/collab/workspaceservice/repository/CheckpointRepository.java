package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Checkpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {
    // Tìm bài nộp của 1 nhóm cho 1 cột mốc (để biết đã nộp chưa)
    Checkpoint findByMilestoneIdAndTeamId(Long milestoneId, String teamId);

    // Lấy danh sách tất cả bài nộp của 1 cột mốc (cho GV xem)
    List<Checkpoint> findByMilestoneId(Long milestoneId);
    
    // Lấy trạng thái nộp của 1 nhóm (để hiện màu xanh trên timeline SV)
    List<Checkpoint> findByTeamId(String teamId);

    @Query("SELECT c.milestoneId, COUNT(c) FROM Checkpoint c WHERE c.milestoneId IN :milestoneIds GROUP BY c.milestoneId")
    List<Object[]> countSubmissionsByMilestoneIds(@Param("milestoneIds") List<Long> milestoneIds);
}
