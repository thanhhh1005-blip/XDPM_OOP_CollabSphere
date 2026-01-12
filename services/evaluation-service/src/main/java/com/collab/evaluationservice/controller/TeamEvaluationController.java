package com.collab.evaluationservice.controller;

import com.collab.evaluationservice.dto.TeamEvaluationRequestDTO;
import com.collab.evaluationservice.dto.TeamEvaluationResponseDTO;
import com.collab.evaluationservice.service.TeamEvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team")
public class TeamEvaluationController {

    private final TeamEvaluationService service;

    public TeamEvaluationController(TeamEvaluationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TeamEvaluationResponseDTO> create(
            @RequestBody TeamEvaluationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createEvaluation(request));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(service.getEvaluationsByTeam(teamId));
    }


    // endpoint test nhanh
    @GetMapping("/ping")
    public String ping() {
        return "evaluation-service OK";
    }
}
