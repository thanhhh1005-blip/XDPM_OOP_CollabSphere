package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.entity.Task;
import com.collab.workspaceservice.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspace/tasks")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository; // Em nhớ tạo interface Repository nhé

    @PostMapping
    public ApiResponse<Task> createTask(@RequestBody Task task) {
        Task savedTask = taskRepository.save(task);
        return new ApiResponse<>(true, "Create success", savedTask);
    }
    
    @GetMapping
    public ApiResponse<Iterable<Task>> getAll() {
        return new ApiResponse<>(true, "List tasks", taskRepository.findAll());
    }
}