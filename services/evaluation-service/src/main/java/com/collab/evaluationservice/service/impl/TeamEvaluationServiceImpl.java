package com.collab.evaluationservice.service.impl;

import com.collab.evaluationservice.dto.TeamEvaluationRequestDTO;
import com.collab.evaluationservice.dto.TeamEvaluationResponseDTO;
import com.collab.evaluationservice.entity.TeamEvaluation;
import com.collab.evaluationservice.repository.TeamEvaluationRepository;
import com.collab.evaluationservice.service.TeamEvaluationService;
import com.collab.evaluationservice.service.event.EvaluationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeamEvaluationServiceImpl implements TeamEvaluationService {

    private final TeamEvaluationRepository repository;
    private final EvaluationEventPublisher eventPublisher;

    public TeamEvaluationServiceImpl(
            TeamEvaluationRepository repository,
            EvaluationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * CREATE evaluation (DTO → Entity → DTO)
     */
    @Override
    public TeamEvaluationResponseDTO createEvaluation(TeamEvaluationRequestDTO request) {

        //  VALIDATION 
        if (request.getScore() == null) {
            throw new IllegalArgumentException("Score không được để trống");
        }

        if (request.getScore() < 1 || request.getScore() > 10) {
            throw new IllegalArgumentException("Score phải nằm trong khoảng 1 - 10");
        }

        if (request.getTeamId() == null) {
            throw new IllegalArgumentException("TeamId không được để trống");
        }

        if (request.getEvaluatorId() == null) {
            throw new IllegalArgumentException("EvaluatorId không được để trống");
        }

        // DTO → ENTITY
        TeamEvaluation entity = new TeamEvaluation();
        entity.setTeamId(request.getTeamId());
        entity.setEvaluatorId(request.getEvaluatorId());
        entity.setScore(request.getScore());
        entity.setComment(request.getComment());
        entity.setCreatedAt(LocalDateTime.now());

        // SAVE DATABASE
        TeamEvaluation saved = repository.save(entity);

        //  PUBLISH EVENT (MICROSERVICE COMMUNICATION)
        eventPublisher.publishEvaluationCreated(saved.getId());

        // ENTITY → RESPONSE DTO
        TeamEvaluationResponseDTO response = new TeamEvaluationResponseDTO();
        response.setId(saved.getId());
        response.setTeamId(saved.getTeamId());
        response.setEvaluatorId(saved.getEvaluatorId());
        response.setScore(saved.getScore());
        response.setComment(saved.getComment());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    /**
     * Tính điểm trung bình team (internal use / future endpoint)
     */
    @Override
    public Double getAverageScoreByTeam(Long teamId) {
        List<TeamEvaluation> evaluations = repository.findByTeamId(teamId);

        if (evaluations.isEmpty()) {
            return 0.0;
        }

        return evaluations.stream()
                .mapToInt(TeamEvaluation::getScore)
                .average()
                .orElse(0.0);
    }
}


        return evaluations.stream()
                .mapToInt(TeamEvaluation::getScore)
                .average()
                .orElse(0.0);
    }
}
