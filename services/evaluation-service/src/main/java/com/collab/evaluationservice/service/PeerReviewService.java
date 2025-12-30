package com.collab.evaluationservice.service;

import com.collab.evaluationservice.dto.PeerReviewRequestDTO;
import com.collab.evaluationservice.dto.PeerReviewResponseDTO;
public interface PeerReviewService {
    PeerReviewResponseDTO create(PeerReviewRequestDTO request);
}
