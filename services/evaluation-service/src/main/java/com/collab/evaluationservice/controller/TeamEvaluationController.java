package com.collab.evaluationservice.controller;

import com.collab.evaluationservice.dto.TeamEvaluationRequestDTO;
import com.collab.evaluationservice.dto.TeamEvaluationResponseDTO;
import com.collab.evaluationservice.service.TeamEvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
public class TeamEvaluationController {

    private final TeamEvaluationService service;

    public TeamEvaluationController(TeamEvaluationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TeamEvaluationResponseDTO> createEvaluation(
            @RequestBody TeamEvaluationRequestDTO request) {

        TeamEvaluationResponseDTO response = service.createEvaluation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
