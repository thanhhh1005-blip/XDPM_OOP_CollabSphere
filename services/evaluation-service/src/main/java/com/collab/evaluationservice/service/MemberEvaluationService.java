package com.collab.evaluationservice.service;

import com.collab.evaluationservice.dto.MemberEvaluationRequestDTO;
import com.collab.evaluationservice.dto.MemberEvaluationResponseDTO;
public interface MemberEvaluationService {
    MemberEvaluationResponseDTO create(MemberEvaluationRequestDTO request);
}
