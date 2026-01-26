package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.entity.SubTask;
import com.collab.workspaceservice.repository.SubTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workspace/subtasks")
@RequiredArgsConstructor
public class SubTaskController {

    private final SubTaskRepository subTaskRepo;

    // 1. Lấy danh sách (SỬA LẠI: Thêm "milestoneId" và "teamId")
    @GetMapping
    public ApiResponse<List<SubTask>> getSubTasks(
            @RequestParam("milestoneId") Long milestoneId, // 👈 Thêm ("milestoneId")
            @RequestParam("teamId") String teamId          // 👈 Thêm ("teamId")
    ) {
        return new ApiResponse<>(1000, "Thành công", subTaskRepo.findByMilestoneIdAndTeamId(milestoneId, teamId));
    }

    // 2. Tạo Checkpoint mới
    @PostMapping
    public ApiResponse<SubTask> create(@RequestBody SubTask req) {
        req.setCompleted(false);
        return new ApiResponse<>(1000, "Tạo checkpoint thành công", subTaskRepo.save(req));
    }

    // 3. Đánh dấu hoàn thành (SỬA LẠI: Thêm "id")
    @PutMapping("/{id}/toggle")
    public ApiResponse<SubTask> toggle(@PathVariable("id") Long id) { // 👈 Thêm ("id")
        SubTask task = subTaskRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy"));
        task.setCompleted(!task.isCompleted());
        return new ApiResponse<>(1000, "Đã cập nhật trạng thái", subTaskRepo.save(task));
    }

    // 4. Xóa Checkpoint (SỬA LẠI: Thêm "id")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) { // 👈 Thêm ("id")
        subTaskRepo.deleteById(id);
        return new ApiResponse<>(1000, "Đã xóa", null);
    }

    @GetMapping("/contribution/{teamId}")
    public ApiResponse<Map<String, Double>> getTeamContribution(@PathVariable("teamId") String teamId) {
        List<SubTask> allTasks = subTaskRepo.findByTeamId(teamId);
        
        // Map: Username -> Danh sách task của họ
        Map<String, List<SubTask>> tasksByUser = allTasks.stream()
                .filter(t -> t.getAssignedTo() != null)
                .collect(Collectors.groupingBy(SubTask::getAssignedTo));

        Map<String, Double> contributionMap = new HashMap<>();

        tasksByUser.forEach((user, tasks) -> {
            long total = tasks.size();
            long completed = tasks.stream().filter(SubTask::isCompleted).count();
            
            // Công thức: (Số task xong / Tổng số task được giao) * 100
            double percent = total == 0 ? 0 : ((double) completed / total) * 100;
            contributionMap.put(user, Math.round(percent * 10.0) / 10.0); // Làm tròn 1 số lẻ
        });
        
        return new ApiResponse<>(1000, "Tính toán thành công", contributionMap);
    }
}