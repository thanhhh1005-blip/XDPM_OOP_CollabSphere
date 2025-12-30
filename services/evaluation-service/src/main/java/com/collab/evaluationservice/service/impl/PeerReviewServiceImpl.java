package com.collab.evaluationservice.service.impl;

import com.collab.evaluationservice.dto.PeerReviewRequestDTO;
import com.collab.evaluationservice.dto.PeerReviewResponseDTO;
import com.collab.evaluationservice.entity.PeerReview;
import com.collab.evaluationservice.repository.PeerReviewRepository;
import com.collab.evaluationservice.service.PeerReviewService;
import com.collab.evaluationservice.service.event.EvaluationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class PeerReviewServiceImpl implements PeerReviewService {

    private final PeerReviewRepository repository;
    private final EvaluationEventPublisher eventPublisher;

    public PeerReviewServiceImpl(
            PeerReviewRepository repository,
            EvaluationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PeerReviewResponseDTO create(PeerReviewRequestDTO request) {

        PeerReview entity = new PeerReview();
        entity.setFromStudentId(request.getFromStudentId());
        entity.setToStudentId(request.getToStudentId());
        entity.setScore(request.getScore());
        entity.setComment(request.getComment());
        entity.setCreatedAt(LocalDateTime.now());

        PeerReview saved = repository.save(entity);

        // publish event
        eventPublisher.publishEvaluationCreated(saved.getId());

        PeerReviewResponseDTO response = new PeerReviewResponseDTO();
        response.setId(saved.getId());
        response.setFromStudentId(saved.getFromStudentId());
        response.setToStudentId(saved.getToStudentId());
        response.setScore(saved.getScore());
        response.setComment(saved.getComment());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }
}
