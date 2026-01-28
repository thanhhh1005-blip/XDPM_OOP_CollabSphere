package com.collab.classroom.client;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestTemplate;

import com.collab.shared.dto.ApiResponse;
import com.collab.shared.dto.WorkspaceCreationRequest;


@Component
@RequiredArgsConstructor
public class WorkspaceServiceClient {
    private final RestTemplate restTemplate;
    private String workspaceServiceUrl = "http://localhost:8080/api/workspace/workspaces";

    public void createClassWorkspace(Long classId) {
        try {
            WorkspaceCreationRequest request = WorkspaceCreationRequest.builder()
                    .classId(classId)
                    .teamId(null)        
                    .settingConfig("{\"type\": \"CLASS_COMMON\"}") 
                    .build();

            restTemplate.postForObject(workspaceServiceUrl, request, ApiResponse.class);
            System.out.println("✅ Đã tạo Common Workspace cho Class: " + classId);

        } catch (Exception e) {
            System.err.println("❌ Lỗi tạo Class Workspace: " + e.getMessage());
        }
    }
}