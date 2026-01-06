package com.collab.evaluationservice.controller;

import com.collab.evaluationservice.dto.MemberEvaluationRequestDTO;
import com.collab.evaluationservice.dto.MemberEvaluationResponseDTO;
import com.collab.evaluationservice.service.MemberEvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/evaluations/member")
public class MemberEvaluationController {

    private final MemberEvaluationService service;

    public MemberEvaluationController(
            MemberEvaluationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MemberEvaluationResponseDTO> create(
            @RequestBody MemberEvaluationRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }
}
