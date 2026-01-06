package com.collab.evaluationservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CheckpointEvaluationResponseDTO {

    private Long id;
    private Long checkpointId;
    private Long evaluatorId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}
