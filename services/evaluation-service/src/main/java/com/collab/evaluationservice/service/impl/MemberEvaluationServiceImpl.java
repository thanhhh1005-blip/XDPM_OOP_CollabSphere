package com.collab.evaluationservice.service.impl;

import com.collab.evaluationservice.dto.MemberEvaluationRequestDTO;
import com.collab.evaluationservice.dto.MemberEvaluationResponseDTO;
import com.collab.evaluationservice.entity.MemberEvaluation;
import com.collab.evaluationservice.repository.MemberEvaluationRepository;
import com.collab.evaluationservice.service.MemberEvaluationService;
import com.collab.evaluationservice.service.event.EvaluationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class MemberEvaluationServiceImpl
        implements MemberEvaluationService {

    private final MemberEvaluationRepository repository;
    private final EvaluationEventPublisher eventPublisher;

    public MemberEvaluationServiceImpl(
            MemberEvaluationRepository repository,
            EvaluationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public MemberEvaluationResponseDTO create(
            MemberEvaluationRequestDTO request) {

        // 1. DTO → Entity
        MemberEvaluation entity = new MemberEvaluation();
        entity.setMemberId(request.getMemberId());
        entity.setEvaluatorId(request.getEvaluatorId());
        entity.setScore(request.getScore());
        entity.setComment(request.getComment());
        entity.setCreatedAt(LocalDateTime.now());

        // 2. Save DB
        MemberEvaluation saved = repository.save(entity);

        // 3. Publish Event
        eventPublisher.publishEvaluationCreated(saved.getId());

        // 4. Entity → Response DTO
        MemberEvaluationResponseDTO response =
                new MemberEvaluationResponseDTO();
        response.setId(saved.getId());
        response.setMemberId(saved.getMemberId());
        response.setScore(saved.getScore());
        response.setComment(saved.getComment());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }
}
