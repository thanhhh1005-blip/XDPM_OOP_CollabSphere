package com.collab.evaluationservice.repository;

import com.collab.evaluationservice.entity.CheckpointEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface CheckpointEvaluationRepository
        extends JpaRepository<CheckpointEvaluation, Long> {

    List<CheckpointEvaluation> findByCheckpointId(Long checkpointId);
}

