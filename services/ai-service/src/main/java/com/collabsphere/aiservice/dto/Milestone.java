package com.collabsphere.aiservice.dto; 
import java.util.List;

public class Milestone {
    private int phase_number;
    private String phase_name;
    private String duration;
    private List<String> tasks;
    private String deliverables;

    // Getter & Setter
    public int getPhase_number() { return phase_number; }
    public void setPhase_number(int phase_number) { this.phase_number = phase_number; }
    public String getPhase_name() { return phase_name; }
    public void setPhase_name(String phase_name) { this.phase_name = phase_name; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public List<String> getTasks() { return tasks; }
    public void setTasks(List<String> tasks) { this.tasks = tasks; }
    public String getDeliverables() { return deliverables; }
    public void setDeliverables(String deliverables) { this.deliverables = deliverables; }
}