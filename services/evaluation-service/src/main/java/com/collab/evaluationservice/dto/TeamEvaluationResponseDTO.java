package com.collab.evaluationservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeamEvaluationResponseDTO {

    private Long id;
    private Long teamId;
    private Long evaluatorId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}
