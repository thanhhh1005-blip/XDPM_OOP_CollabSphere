package com.collab.evaluationservice.dto;

import lombok.Data;

@Data
public class PeerReviewRequestDTO {

    private Long fromStudentId;
    private Long toStudentId;
    private Integer score;
    private String comment;
}
