package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.shared.dto.WorkspaceCreationRequest; // Đảm bảo đã import DTO này
import com.collab.workspaceservice.entity.Workspace;
import com.collab.workspaceservice.repository.WorkspaceRepository;
import com.collab.workspaceservice.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspace/workspaces")
@RequiredArgsConstructor 
public class WorkspaceController {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;

   
    @PostMapping
    public ApiResponse<Workspace> createWorkspace(@RequestBody WorkspaceCreationRequest request) {
        return ApiResponse.<Workspace>builder()
                .code(1000)
                .message("Tạo Workspace thành công")
                .result(workspaceService.createWorkspace(request))
                .build();
    }

    @GetMapping("/team/{teamId}")
    public ApiResponse<Workspace> getByTeam(@PathVariable("teamId") String teamId) {
        Workspace ws = workspaceRepository.findByTeamId(teamId).orElse(null);
        
        if (ws == null) {
            return new ApiResponse<>(1001, "Nhóm này chưa có Workspace", null);
        }
        return new ApiResponse<>(1000, "Workspace của nhóm", ws);
    }

    @GetMapping("/class/{classId}")
    public ApiResponse<Workspace> getByClass(@PathVariable("classId") Long classId) {
        Workspace ws = workspaceRepository.findByClassIdAndTeamIdIsNull(classId).orElse(null);
        
        if (ws == null) {
            return new ApiResponse<>(1001, "Lớp này chưa kích hoạt không gian chung", null);
        }
        return new ApiResponse<>(1000, "Workspace chung của lớp", ws);
    }

    @GetMapping("/{id}")
    public ApiResponse<Workspace> getById(@PathVariable("id") Long id) {
        return new ApiResponse<>(1000, "Thành công", workspaceRepository.findById(id).orElse(null));
    }

    @GetMapping
    public ApiResponse<Iterable<Workspace>> getAll() {
        return new ApiResponse<>(1000, "All Workspaces", workspaceRepository.findAll());
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteWorkspace(@PathVariable Long id) {
        workspaceRepository.deleteById(id);
        return new ApiResponse<>(1000, "Đã xóa Workspace", null);
    }
}