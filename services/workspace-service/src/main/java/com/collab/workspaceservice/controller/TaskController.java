package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.entity.Sprint;
import com.collab.workspaceservice.entity.Task;
import com.collab.workspaceservice.repository.SprintRepository;
import com.collab.workspaceservice.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspace/tasks")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired // <--- THÊM CÁI NÀY
    private SprintRepository sprintRepository;

    @PostMapping
    public ApiResponse<Task> createTask(@RequestBody Task task) {
        Task savedTask = taskRepository.save(task);
        return new ApiResponse<>(true, "Create success", savedTask);
    }
    
    // API quan trọng nhất: Cập nhật Status VÀ Sprint
    @PutMapping("/{taskId}/status")
    public ApiResponse<Task> updateTaskStatus(
            @PathVariable("taskId") Long taskId, 
            @RequestParam("status") String status,
            @RequestParam(name = "sprintId", required = false) Long sprintId // <--- THÊM THAM SỐ NÀY
    ) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return new ApiResponse<>(false, "Task not found", null);
        }
        
        // 1. Cập nhật trạng thái
        task.setStatus(status);
        
        // 2. Cập nhật Sprint (ĐÂY LÀ ĐOẠN EM ĐANG THIẾU)
        if (sprintId != null) {
            Sprint sprint = sprintRepository.findById(sprintId).orElse(null);
            task.setSprint(sprint);
        } else if ("BACKLOG".equals(status)) {
            task.setSprint(null); // Về kho thì xóa sprint
        }

        return new ApiResponse<>(true, "Status updated", taskRepository.save(task));
    }
    
    // API lấy Task (Hỗ trợ lọc)
    @GetMapping
    public ApiResponse<Iterable<Task>> getTasks(@RequestParam(name = "sprintId", required = false) Long sprintId) {
        if (sprintId != null) {
            return new ApiResponse<>(true, "Tasks by sprint", taskRepository.findBySprintId(sprintId));
        }
        return new ApiResponse<>(true, "All tasks", taskRepository.findAll());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ApiResponse<Void> deleteTask(@PathVariable("id") Long id) {
        taskRepository.deleteById(id);
        return new ApiResponse<>(true, "Task deleted", null);
    }
}