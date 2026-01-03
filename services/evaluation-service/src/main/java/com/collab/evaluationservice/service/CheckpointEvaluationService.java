package com.collab.evaluationservice.service;

import java.util.List;

import com.collab.evaluationservice.dto.CheckpointEvaluationRequestDTO;
import com.collab.evaluationservice.dto.CheckpointEvaluationResponseDTO;

public interface CheckpointEvaluationService {
    CheckpointEvaluationResponseDTO create(
            CheckpointEvaluationRequestDTO request);
}

