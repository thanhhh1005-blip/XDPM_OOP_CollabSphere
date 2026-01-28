package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.entity.Sprint;
import com.collab.workspaceservice.entity.Task;
import com.collab.workspaceservice.entity.Workspace;
import com.collab.workspaceservice.repository.SprintRepository;
import com.collab.workspaceservice.repository.TaskRepository;
import com.collab.workspaceservice.repository.WorkspaceRepository;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspace/tasks")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired 
    private SprintRepository sprintRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @PostMapping
    public ApiResponse<Task> createTask(
        @RequestBody Task task,
        @RequestParam("workspaceId") Long workspaceId,
        @RequestParam("teamId") String teamId, // <--- SỬA: Nhận String
        @RequestParam(name = "classId", required = false) Long classId
    ) {
        Workspace ws = workspaceRepository.findById(workspaceId).orElse(null);
        task.setWorkspace(ws);
        
        if (teamId != null) task.setTeamId(teamId);
        if (classId != null) task.setClassId(classId);
        task.setStatus("BACKLOG");
        return new ApiResponse<>(1000, "Create success", taskRepository.save(task));
    }
    
    @PutMapping("/{taskId}/status")
    public ApiResponse<Task> updateTaskStatus(
            @PathVariable("taskId") Long taskId, 
            @RequestParam("status") String status,
            @RequestParam(name = "sprintId", required = false) Long sprintId 
    ) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return new ApiResponse<>(1000, "Task not found", null);
        }
        
        task.setStatus(status);
        
        if (sprintId != null) {
            Sprint sprint = sprintRepository.findById(sprintId).orElse(null);
            task.setSprint(sprint);
        } else if ("BACKLOG".equals(status)) {
            task.setSprint(null);
        }

        return new ApiResponse<>(1000, "Status updated", taskRepository.save(task));
    }
    
    @PutMapping("/{id}/assign")
    @Transactional
    public ApiResponse<Task> assignTask(
        @PathVariable("id") Long id,
        @RequestParam("assigneeId") String assigneeId
    ) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setAssigneeId(assigneeId);
        return new ApiResponse<>(1000, "Assigned successfully", taskRepository.save(task));
    }
    @GetMapping
    public ApiResponse<Iterable<Task>> getTasks(
            @RequestParam("workspaceId") Long workspaceId,
            @RequestParam(name = "teamId", required = false) String teamId, // <--- SỬA: Nhận String
            @RequestParam(name = "classId", required = false) Long classId
    ) {
        if (teamId != null && !teamId.isEmpty()) {
            return new ApiResponse<>(1000, "Dữ liệu Task theo Team", 
                    taskRepository.findByWorkspaceIdAndTeamId(workspaceId, teamId));
        }
        if (classId != null) {
            return new ApiResponse<>(1000, "Dữ liệu Task theo Class", 
                    taskRepository.findByWorkspaceIdAndClassId(workspaceId, classId));
        }
        return new ApiResponse<>(1000, "Dữ liệu Task (All)", 
                taskRepository.findByWorkspaceId(workspaceId));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ApiResponse<Void> deleteTask(@PathVariable("id") Long id) {
        taskRepository.deleteById(id);
        return new ApiResponse<>(1000, "Task deleted", null);
    }
}