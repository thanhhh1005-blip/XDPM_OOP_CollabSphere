package com.collab.evaluationservice.service;

import com.collab.evaluationservice.dto.TeamEvaluationRequestDTO;
import com.collab.evaluationservice.dto.TeamEvaluationResponseDTO;
import com.collab.evaluationservice.entity.TeamEvaluation;

import java.util.List;

public interface TeamEvaluationService {

    TeamEvaluationResponseDTO createEvaluation(TeamEvaluationRequestDTO request);

    List<TeamEvaluation> getEvaluationsByTeam(Long teamId);

    Double getAverageScoreByTeam(Long teamId);
}


