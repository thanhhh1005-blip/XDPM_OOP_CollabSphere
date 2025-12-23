package com.collab.evaluationservice.repository;

import com.collab.evaluationservice.entity.TeamEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamEvaluationRepository
        extends JpaRepository<TeamEvaluation, Long> {

    // Lấy tất cả đánh giá của 1 team
    List<TeamEvaluation> findByTeamId(Long teamId);

    // Lấy đánh giá do 1 người thực hiện
    List<TeamEvaluation> findByEvaluatorId(Long evaluatorId);
}
