ppackage com.collab.evaluationservice.service;

import com.collab.evaluationservice.dto.TeamEvaluationRequestDTO;
import com.collab.evaluationservice.dto.TeamEvaluationResponseDTO;

public interface TeamEvaluationService {

    TeamEvaluationResponseDTO createEvaluation(TeamEvaluationRequestDTO request);

    Double getAverageScoreByTeam(Long teamId);
}


