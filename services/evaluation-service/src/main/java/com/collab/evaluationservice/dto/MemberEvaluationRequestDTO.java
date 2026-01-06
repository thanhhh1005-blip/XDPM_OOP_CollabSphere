package com.collab.evaluationservice.dto;

import lombok.Data;

@Data
public class MemberEvaluationRequestDTO {

    private Long memberId;
    private Long evaluatorId;
    private Integer score;
    private String comment;
}
