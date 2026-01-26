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
@RequiredArgsConstructor // Tự động Inject Service và Repository (Thay cho @Autowired)
public class WorkspaceController {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;

    // ==================================================================
    // 1. TẠO MỚI (Dùng chung cho cả Team và Class)
    // ==================================================================
    // URL: POST /api/workspace/workspaces
    // Body: { "classId": 10, "teamId": "uuid..." } hoặc { "classId": 10, "teamId": null }
    @PostMapping
    public ApiResponse<Workspace> createWorkspace(@RequestBody WorkspaceCreationRequest request) {
        // Logic check trùng và tạo mới đã nằm hết bên Service
        return ApiResponse.<Workspace>builder()
                .code(1000)
                .message("Tạo Workspace thành công")
                .result(workspaceService.createWorkspace(request))
                .build();
    }

    // ==================================================================
    // 2. CÁC API GET (Lấy dữ liệu)
    // ==================================================================

    // A. Lấy Workspace của một TEAM cụ thể
    // URL: GET /api/workspace/workspaces/team/{teamId}
    @GetMapping("/team/{teamId}")
    public ApiResponse<Workspace> getByTeam(@PathVariable("teamId") String teamId) {
        Workspace ws = workspaceRepository.findByTeamId(teamId).orElse(null);
        
        if (ws == null) {
            return new ApiResponse<>(1001, "Nhóm này chưa có Workspace", null);
        }
        return new ApiResponse<>(1000, "Workspace của nhóm", ws);
    }

    // B. Lấy Workspace chung của LỚP (Sửa lại logic chuẩn)
    // URL: GET /api/workspace/workspaces/class/{classId}
    @GetMapping("/class/{classId}")
    public ApiResponse<Workspace> getByClass(@PathVariable("classId") Long classId) {
        // 👇 QUAN TRỌNG: Phải tìm cái nào có teamId = null
        Workspace ws = workspaceRepository.findByClassIdAndTeamIdIsNull(classId).orElse(null);
        
        if (ws == null) {
            // Tùy chọn: Nếu chưa có thì có thể trả về null hoặc tự tạo mới luôn ở đây cũng được
            return new ApiResponse<>(1001, "Lớp này chưa kích hoạt không gian chung", null);
        }
        return new ApiResponse<>(1000, "Workspace chung của lớp", ws);
    }

    // C. Lấy chi tiết theo ID Workspace (Dùng khi F5 trang, load lại theo ID)
    @GetMapping("/{id}")
    public ApiResponse<Workspace> getById(@PathVariable("id") Long id) {
        return new ApiResponse<>(1000, "Thành công", workspaceRepository.findById(id).orElse(null));
    }

    // D. Lấy tất cả (Debug)
    @GetMapping
    public ApiResponse<Iterable<Workspace>> getAll() {
        return new ApiResponse<>(1000, "All Workspaces", workspaceRepository.findAll());
    }

    // ==================================================================
    // 3. XÓA
    // ==================================================================
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteWorkspace(@PathVariable Long id) {
        workspaceRepository.deleteById(id);
        return new ApiResponse<>(1000, "Đã xóa Workspace", null);
    }
}