package com.collabsphere.aiservice.dto.response;

import java.util.List;

public class MilestoneResponse {
    private String projectName;
    private List<Milestone> milestones;

    // Constructor rỗng
    public MilestoneResponse() {}

    // Inner class (Class con)
    public static class Milestone {
        private String title;
        private String description;
        private String duration;

        public Milestone() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
    }

    // Getter Setter lớp cha
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public List<Milestone> getMilestones() { return milestones; }
    public void setMilestones(List<Milestone> milestones) { this.milestones = milestones; }
}