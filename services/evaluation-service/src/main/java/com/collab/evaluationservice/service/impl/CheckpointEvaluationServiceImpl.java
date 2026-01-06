package com.collab.evaluationservice.service.impl;

import com.collab.evaluationservice.dto.CheckpointEvaluationRequestDTO;
import com.collab.evaluationservice.dto.CheckpointEvaluationResponseDTO;
import com.collab.evaluationservice.entity.CheckpointEvaluation;
import com.collab.evaluationservice.repository.CheckpointEvaluationRepository;
import com.collab.evaluationservice.service.CheckpointEvaluationService;
import com.collab.evaluationservice.service.event.EvaluationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class CheckpointEvaluationServiceImpl
        implements CheckpointEvaluationService {

    private final CheckpointEvaluationRepository repository;
    private final EvaluationEventPublisher eventPublisher;

    public CheckpointEvaluationServiceImpl(
            CheckpointEvaluationRepository repository,
            EvaluationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CheckpointEvaluationResponseDTO create(
            CheckpointEvaluationRequestDTO request) {

        CheckpointEvaluation entity = new CheckpointEvaluation();
        entity.setCheckpointId(request.getCheckpointId());
        entity.setEvaluatorId(request.getEvaluatorId());
        entity.setScore(request.getScore());
        entity.setComment(request.getComment());
        entity.setCreatedAt(LocalDateTime.now());

        CheckpointEvaluation saved = repository.save(entity);

        eventPublisher.publishEvaluationCreated(saved.getId());

        CheckpointEvaluationResponseDTO response =
                new CheckpointEvaluationResponseDTO();
        response.setId(saved.getId());
        response.setCheckpointId(saved.getCheckpointId());
        response.setScore(saved.getScore());
        response.setComment(saved.getComment());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }
}

