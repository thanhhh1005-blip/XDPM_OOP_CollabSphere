package com.collab.evaluationservice.controller;

import com.collab.evaluationservice.dto.CheckpointEvaluationRequestDTO;
import com.collab.evaluationservice.dto.CheckpointEvaluationResponseDTO;
import com.collab.evaluationservice.service.CheckpointEvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/evaluations/checkpoint")
public class CheckpointEvaluationController {

    private final CheckpointEvaluationService service;

    public CheckpointEvaluationController(
            CheckpointEvaluationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CheckpointEvaluationResponseDTO> create(
            @RequestBody CheckpointEvaluationRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }
}
