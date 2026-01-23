package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.entity.Sprint;
import com.collab.workspaceservice.entity.Task;
import com.collab.workspaceservice.repository.MilestoneRepository;
import com.collab.workspaceservice.repository.SprintRepository;
import com.collab.workspaceservice.repository.TaskRepository;
import com.collab.workspaceservice.repository.WorkspaceRepository;

import java.util.ArrayList;
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
    @Autowired 
    private WorkspaceRepository workspaceRepository;

    @PostMapping
    public ApiResponse<Sprint> createSprint(@RequestBody Sprint sprint) {
        // Nếu Frontend gửi workspace id dạng object lồng
        if(sprint.getWorkspace() != null && sprint.getWorkspace().getId() != null) {
             // Đảm bảo set đủ thông tin workspace nếu cần
        }
        return new ApiResponse<>(1000, "Create success", sprintRepository.save(sprint));
    }

    @GetMapping("/project/{projectId}")
    public ApiResponse<Iterable<Sprint>> getSprintsByProject(@PathVariable Long projectId) {
        return new ApiResponse<>(1000, "List sprints", sprintRepository.findByProjectId(projectId));
    }

    // --- ĐÃ XÓA HÀM CŨ TẠI ĐÂY ĐỂ TRÁNH LỖI ---

    @GetMapping
    public ApiResponse<Iterable<Sprint>> getAllSprints() {
        return new ApiResponse<>(1000, "List sprints", sprintRepository.findAll());
    }

    @GetMapping("/milestone/{msId}")
    public ApiResponse<Iterable<Sprint>> getSprintsByMilestone(@PathVariable("msId") Long msId) {
        return new ApiResponse<>(1000, "Sprints in Milestone", sprintRepository.findByMilestoneId(msId));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ApiResponse<Void> deleteSprint(@PathVariable("id") Long id) {
        // Giải phóng các task đang thuộc sprint này về lại Backlog
        List<Task> tasks = taskRepository.findBySprintId(id);
        for (Task t : tasks) {
            t.setSprint(null);
            taskRepository.save(t);
        }
        sprintRepository.deleteById(id);
        return new ApiResponse<>(1000, "Đã xóa Sprint thành công", null);
    }

    // ✅ GIỮ LẠI HÀM MỚI NÀY (Hỗ trợ lọc theo classId và teamId)
    @GetMapping("/by-workspace/{workspaceId}")
    public ApiResponse<List<Sprint>> getSprintsByWorkspace(
            @PathVariable("workspaceId") Long workspaceId, // <--- THÊM ("workspaceId") VÀO ĐÂY
            @RequestParam(name = "classId", required = false) Long classId,
            @RequestParam(name = "teamId", required = false) String teamId
    ) {
        List<Sprint> sprints = new ArrayList<>();

        if (classId != null) {
            sprints = sprintRepository.findByWorkspaceIdAndClassId(workspaceId, classId);
        } else if (teamId != null && !teamId.isEmpty()) {
            sprints = sprintRepository.findByWorkspaceIdAndTeamId(workspaceId, teamId);
        } else {
            // Trường hợp không có cả 2 -> Trả về rỗng
        }

        return new ApiResponse<>(1000, "Success", sprints);
    }
}