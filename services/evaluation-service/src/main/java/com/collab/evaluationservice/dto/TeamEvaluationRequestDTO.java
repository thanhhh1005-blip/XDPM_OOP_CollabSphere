package com.collab.evaluationservice.dto;

import lombok.Data;

@Data
public class TeamEvaluationRequestDTO {

    private Long teamId;
    private Long evaluatorId;
    private Integer score;
    private String comment;
}
