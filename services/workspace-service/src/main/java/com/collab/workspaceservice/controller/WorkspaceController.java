package com.collab.workspaceservice.controller;

import com.collab.shared.dto.ApiResponse;
import com.collab.workspaceservice.entity.Workspace;
import com.collab.workspaceservice.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspace/workspaces")
public class WorkspaceController {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    // 1. API: Kích hoạt Workspace cho một Team (Gọi khi GV tạo nhóm)
    // URL: POST /api/workspace/workspaces/team/10
    @PostMapping("/team/{teamId}")
    public ApiResponse<Workspace> createForTeam(
        @PathVariable("teamId") String teamId,
        @RequestParam("classId") Long classId // Nhận thêm classId từ bên gọi
    ) {
        if (workspaceRepository.existsByTeamId(teamId)) {
            return new ApiResponse<>(1001, "Nhóm đã có Workspace", null);
        }
        
        Workspace ws = new Workspace();
        ws.setTeamId(teamId);
        ws.setClassId(classId); // Lưu cả thông tin lớp
        
        return new ApiResponse<>(1000, "Kích hoạt Workspace thành công", workspaceRepository.save(ws));
    }

    // 2. API: Tìm Workspace theo Team ID (Frontend dùng cái này để dẫn SV vào đúng nhà)
    // URL: GET /api/workspace/workspaces/team/10
    @GetMapping("/team/{teamId}")
    public ApiResponse<Workspace> getByTeam(@PathVariable("teamId") String teamId) {
        Workspace ws = workspaceRepository.findByTeamId(teamId).orElse(null);
        System.out.println(">>> BACKEND NHẬN ĐƯỢC TEAM_ID: [" + teamId + "]");
        if (ws == null) {
            System.out.println(">>> KẾT QUẢ: KHÔNG TÌM THẤY TRONG DB!");
            return new ApiResponse<>(1000, "Nhóm này chưa có Workspace", null);
            
        }
        System.out.println(">>> KẾT QUẢ: ĐÃ THẤY WORKSPACE ID = " + ws.getId());
        return new ApiResponse<>(1000, "Workspace của nhóm", ws);
    }
    
    // 3. API: Lấy tất cả (Dùng cho Admin hoặc debug)
    @GetMapping
    public ApiResponse<Iterable<Workspace>> getAll() {
        return new ApiResponse<>(1000, "Danh sách tất cả Workspace", workspaceRepository.findAll());
    }

    // 4. API: Xóa Workspace (Khi giải tán nhóm)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteWorkspace(@PathVariable Long id) {
        workspaceRepository.deleteById(id);
        return new ApiResponse<>(1000, "Đã xóa Workspace", null);
    }

    // Thêm vào WorkspaceController.java
    @GetMapping("/{id}")
    public ApiResponse<Workspace> getById(@PathVariable("id") Long id) {
        // Trả về cả cục Workspace, trong đó có chứa teamId (mã UUID)
        return new ApiResponse<>(1000, "Thành công", workspaceRepository.findById(id).orElse(null));
    }

    @GetMapping("/class/{classId}")
    public ApiResponse<Workspace> getByClass(@PathVariable("classId") Long classId) {
        Workspace ws = workspaceRepository.findByClassId(classId).orElse(null);
        return new ApiResponse<>(1000, "Workspace của lớp", ws);
    }
}