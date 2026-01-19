package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.entity.Workspace;
import com.collab.workspaceservice.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspace/workspaces")
@CrossOrigin("*") // Cho phép Frontend gọi vào
public class WorkspaceController {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    // 1. API: Kích hoạt Workspace cho một Team (Gọi khi GV tạo nhóm)
    // URL: POST /api/workspace/workspaces/team/10
    @PostMapping("/team/{teamId}")
    public ApiResponse<Workspace> createForTeam(@PathVariable Long teamId) {
        // Kiểm tra xem nhóm này đã có workspace chưa để tránh tạo trùng
        if (workspaceRepository.existsByTeamId(teamId)) {
            return new ApiResponse<>(false, "Nhóm này đã có không gian làm việc rồi!", null);
        }
        
        Workspace ws = new Workspace();
        ws.setTeamId(teamId);
        // Có thể set thêm config mặc định nếu cần
        
        return new ApiResponse<>(true, "Kích hoạt Workspace thành công", workspaceRepository.save(ws));
    }

    // 2. API: Tìm Workspace theo Team ID (Frontend dùng cái này để dẫn SV vào đúng nhà)
    // URL: GET /api/workspace/workspaces/team/10
    @GetMapping("/team/{teamId}")
    public ApiResponse<Workspace> getByTeam(@PathVariable Long teamId) {
        Workspace ws = workspaceRepository.findByTeamId(teamId).orElse(null);
        if (ws == null) {
            return new ApiResponse<>(false, "Nhóm này chưa có Workspace", null);
        }
        return new ApiResponse<>(true, "Workspace của nhóm", ws);
    }
    
    // 3. API: Lấy tất cả (Dùng cho Admin hoặc debug)
    @GetMapping
    public ApiResponse<Iterable<Workspace>> getAll() {
        return new ApiResponse<>(true, "Danh sách tất cả Workspace", workspaceRepository.findAll());
    }

    // 4. API: Xóa Workspace (Khi giải tán nhóm)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteWorkspace(@PathVariable Long id) {
        workspaceRepository.deleteById(id);
        return new ApiResponse<>(true, "Đã xóa Workspace", null);
    }
}