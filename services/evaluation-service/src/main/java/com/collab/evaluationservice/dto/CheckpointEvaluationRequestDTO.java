package com.collab.evaluationservice.dto;

import lombok.Data;

@Data
public class CheckpointEvaluationRequestDTO {

    private Long checkpointId;
    private Long evaluatorId;
    private Integer score;
    private String comment;
}
