package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.entity.Milestone;
import com.collab.workspaceservice.entity.Sprint;
import com.collab.workspaceservice.entity.Task;
import com.collab.workspaceservice.repository.MilestoneRepository;
import com.collab.workspaceservice.repository.SprintRepository;
import com.collab.workspaceservice.repository.TaskRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspace/sprints")
public class SprintController {
    @Autowired
    private SprintRepository sprintRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MilestoneRepository milestoneRepository;

    @PostMapping
    public ApiResponse<Sprint> createSprint(
            @RequestBody Sprint sprint,
            @RequestParam(name = "milestoneId", required = false) Long milestoneId // Thêm tham số
    ) {
        if (milestoneId != null) {
            // Tìm Milestone và gán vào
            // (Em cần @Autowired MilestoneRepository vào SprintController nhé)
            Milestone ms = milestoneRepository.findById(milestoneId).orElse(null);
            sprint.setMilestone(ms);
        }
        return new ApiResponse<>(1000, "Created sprint", sprintRepository.save(sprint));
    }

    @GetMapping("/project/{projectId}")
    public ApiResponse<Iterable<Sprint>> getSprintsByProject(@PathVariable Long projectId) {
        return new ApiResponse<>(1000, "List sprints", sprintRepository.findByProjectId(projectId));
    }
    @GetMapping
    public ApiResponse<Iterable<Sprint>> getAllSprints() {
        return new ApiResponse<>(1000, "List sprints", sprintRepository.findAll());
    }
    @GetMapping("/milestone/{msId}")
    public ApiResponse<Iterable<Sprint>> getSprintsByMilestone(@PathVariable("msId") Long msId) {
        // Em cần thêm hàm findByMilestoneId vào SprintRepository
        return new ApiResponse<>(1000, "Sprints in Milestone", sprintRepository.findByMilestoneId(msId));
    }
    // API Xóa Sprint
    @DeleteMapping("/{id}")
    @Transactional
    public ApiResponse<Void> deleteSprint(@PathVariable("id") Long id) {
        // BƯỚC 1: Tìm tất cả task của sprint này và set sprint = null (thả về Backlog)
        List<Task> tasks = taskRepository.findBySprintId(id);
        for (Task t : tasks) {
            t.setSprint(null);
            taskRepository.save(t);
        }
        
        // BƯỚC 2: Xóa Sprint
        sprintRepository.deleteById(id);
        return new ApiResponse<>(1000, "Sprint deleted", null);
    }
}