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

    @Override
    public TeamEvaluationResponseDTO createEvaluation(TeamEvaluationRequestDTO request) {

        if (request.getScore() == null || request.getScore() < 1 || request.getScore() > 10) {
            throw new IllegalArgumentException("Score phải từ 1 đến 10");
        }

        TeamEvaluation entity = new TeamEvaluation();
        entity.setTeamId(request.getTeamId());
        entity.setEvaluatorId(request.getEvaluatorId());
        entity.setScore(request.getScore());
        entity.setComment(request.getComment());
        entity.setCreatedAt(LocalDateTime.now());

        TeamEvaluation saved = repository.save(entity);

        eventPublisher.publishEvaluationCreated(saved.getId());

        TeamEvaluationResponseDTO response = new TeamEvaluationResponseDTO();
        response.setId(saved.getId());
        response.setTeamId(saved.getTeamId());
        response.setEvaluatorId(saved.getEvaluatorId());
        response.setScore(saved.getScore());
        response.setComment(saved.getComment());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    @Override
    public List<TeamEvaluation> getEvaluationsByTeam(Long teamId) {
        return repository.findByTeamId(teamId);
    }

    @Override
    public Double getAverageScoreByTeam(Long teamId) {
        return repository.findByTeamId(teamId)
                .stream()
                .mapToInt(TeamEvaluation::getScore)
                .average()
                .orElse(0.0);
    }
}
