package com.collab.evaluationservice.controller;

import com.collab.evaluationservice.dto.PeerReviewRequestDTO;
import com.collab.evaluationservice.dto.PeerReviewResponseDTO;
import com.collab.evaluationservice.service.PeerReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/evaluations/peer")
public class PeerReviewController {

    private final PeerReviewService service;

    public PeerReviewController(PeerReviewService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PeerReviewResponseDTO> create(
            @RequestBody PeerReviewRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }
}
