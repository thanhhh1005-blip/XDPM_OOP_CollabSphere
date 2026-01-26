package com.collab.workspaceservice.service;

import org.springframework.stereotype.Service;

import com.collab.shared.dto.WorkspaceCreationRequest;
import com.collab.workspaceservice.entity.Workspace;
import com.collab.workspaceservice.repository.WorkspaceRepository;

import lombok.RequiredArgsConstructor;

// File: com.collab.workspaceservice.service.WorkspaceService

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    // 👇 SỬA/THÊM HÀM createWorkspace
    public Workspace createWorkspace(WorkspaceCreationRequest request) {
        
        // 1. Kiểm tra xem Workspace này đã tồn tại chưa để tránh trùng lặp
        if (request.getTeamId() != null) {
            // Case 1: Tạo cho Team -> Check xem team này có workspace chưa
            if (workspaceRepository.existsByTeamId(request.getTeamId())) {
                throw new RuntimeException("Workspace cho Team này đã tồn tại!");
            }
        } else {
            // Case 2: Tạo cho Lớp (Workspace chung) -> Check xem lớp này có workspace chung chưa
            // Lưu ý: Cần đảm bảo Repository có hàm existsByClassIdAndTeamIdIsNull
            if (workspaceRepository.existsByClassIdAndTeamIdIsNull(request.getClassId())) {
                // Nếu có rồi thì thôi, trả về cái cũ hoặc báo lỗi tùy bạn. 
                // Ở đây mình return luôn cái cũ để code không bị lỗi 500 nếu lỡ gọi 2 lần.
                // Cách 1: An toàn nhất (Khuyên dùng)
                return workspaceRepository.findByClassIdAndTeamIdIsNull(request.getClassId()).orElse(null);
            }
        }

        // 2. Tạo mới
        Workspace workspace = new Workspace();
        workspace.setClassId(request.getClassId());
        workspace.setTeamId(request.getTeamId()); // Có thể null
        workspace.setSettingConfig(request.getSettingConfig());
        
        return workspaceRepository.save(workspace);
    }

    
}
