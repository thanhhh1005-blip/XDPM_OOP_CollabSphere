package com.collab.teamservice.client;

import com.collab.shared.dto.ApiResponse; // Dòng này sẽ hết đỏ sau khi làm PHẦN 1

import lombok.RequiredArgsConstructor;
import com.collab.shared.dto.WorkspaceCreationRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor // Tự inject RestTemplate
public class WorkspaceServiceClient {
    private final RestTemplate restTemplate;
     // Nên để trong application.properties
    private String workspaceServiceUrl = "http://localhost:8080/api/workspace/workspaces";

    public void createTeamWorkspace(String teamId, Long classId) {
        try {
            // "Vibe" code: Dùng Builder pattern gọn gàng, tường minh
            WorkspaceCreationRequest request = WorkspaceCreationRequest.builder()
                    .teamId(teamId)       // Gửi ID nhóm
                    .classId(classId)     // Gửi ID lớp
                    .settingConfig("{}")  // Config mặc định
                    .build();

            restTemplate.postForObject(workspaceServiceUrl, request, ApiResponse.class);
            System.out.println("✅ Đã tạo Workspace cho Team: " + teamId);

        } catch (Exception e) {
            System.err.println("❌ Lỗi tạo Team Workspace: " + e.getMessage());
            // Có thể throw exception để Rollback transaction tạo Team nếu cần chặt chẽ
        }
    }
}