package com.collabsphere.aiservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ProjectPlanResponse {

    // Ánh xạ trường JSON "project_name" vào biến Java "projectName"
    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("milestones")
    private List<Milestone> milestones;

    // ==========================
    // GETTERS & SETTERS
    // ==========================
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    // ==========================
    // INNER CLASS: MILESTONE
    // (Định nghĩa cấu trúc của từng giai đoạn)
    // ==========================
    public static class Milestone {
        
        @JsonProperty("phase_number")
        private int phaseNumber;

        @JsonProperty("phase_name")
        private String phaseName;

        @JsonProperty("duration")
        private String duration;

        @JsonProperty("description")
        private String description; // Mô tả giai đoạn (nếu AI có trả về)

        @JsonProperty("tasks")
        private List<String> tasks;

        @JsonProperty("deliverables")
        private String deliverables;

        // Getters & Setters cho Milestone
        public int getPhaseNumber() { return phaseNumber; }
        public void setPhaseNumber(int phaseNumber) { this.phaseNumber = phaseNumber; }

        public String getPhaseName() { return phaseName; }
        public void setPhaseName(String phaseName) { this.phaseName = phaseName; }

        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public List<String> getTasks() { return tasks; }
        public void setTasks(List<String> tasks) { this.tasks = tasks; }

        public String getDeliverables() { return deliverables; }
        public void setDeliverables(String deliverables) { this.deliverables = deliverables; }
    }
}