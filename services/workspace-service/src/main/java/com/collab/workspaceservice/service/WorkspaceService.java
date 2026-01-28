package com.collab.workspaceservice.service;

import org.springframework.stereotype.Service;

import com.collab.shared.dto.WorkspaceCreationRequest;
import com.collab.workspaceservice.entity.Workspace;
import com.collab.workspaceservice.repository.WorkspaceRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public Workspace createWorkspace(WorkspaceCreationRequest request) {
        
        if (request.getTeamId() != null) {
            if (workspaceRepository.existsByTeamId(request.getTeamId())) {
                throw new RuntimeException("Workspace cho Team này đã tồn tại!");
            }
        } else {
            if (workspaceRepository.existsByClassIdAndTeamIdIsNull(request.getClassId())) {
                return workspaceRepository.findByClassIdAndTeamIdIsNull(request.getClassId()).orElse(null);
            }
        }

        // 2. Tạo mới
        Workspace workspace = new Workspace();
        workspace.setClassId(request.getClassId());
        workspace.setTeamId(request.getTeamId()); 
        workspace.setSettingConfig(request.getSettingConfig());
        
        return workspaceRepository.save(workspace);
    }

    
}
