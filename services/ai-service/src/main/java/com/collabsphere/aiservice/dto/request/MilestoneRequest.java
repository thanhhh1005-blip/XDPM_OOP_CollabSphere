package com.collabsphere.aiservice.dto.request;

public class MilestoneRequest {
    private String syllabus;

    public MilestoneRequest() {
    }

    public MilestoneRequest(String syllabus) {
        this.syllabus = syllabus;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }
}