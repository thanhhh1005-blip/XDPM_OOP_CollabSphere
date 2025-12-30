package com.collab.evaluationservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MemberEvaluationResponseDTO {

    private Long id;
    private Long memberId;
    private Long evaluatorId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}
