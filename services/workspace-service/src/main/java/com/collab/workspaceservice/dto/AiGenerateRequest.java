package com.collab.workspaceservice.dto;

import lombok.Data;

@Data
public class AiGenerateRequest {
    private String syllabusContent;
    private int durationWeeks;
    private Long classId;
}