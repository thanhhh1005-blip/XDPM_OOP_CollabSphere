package com.collab.evaluationservice.service;

import com.collab.evaluationservice.dto.CheckpointEvaluationRequestDTO;
import com.collab.evaluationservice.dto.CheckpointEvaluationResponseDTO;

public interface CheckpointEvaluationService {
    CheckpointEvaluationResponseDTO create(
            CheckpointEvaluationRequestDTO request);
}

