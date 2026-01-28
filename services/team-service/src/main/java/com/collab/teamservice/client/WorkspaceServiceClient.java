package com.collab.teamservice.client;

import com.collab.shared.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import com.collab.shared.dto.WorkspaceCreationRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor 
public class WorkspaceServiceClient {
    private final RestTemplate restTemplate;
    private String workspaceServiceUrl = "http://localhost:8080/api/workspace/workspaces";

    public void createTeamWorkspace(String teamId, Long classId) {
        try {
            WorkspaceCreationRequest request = WorkspaceCreationRequest.builder()
                    .teamId(teamId)      
                    .classId(classId)     
                    .settingConfig("{}") 
                    .build();

            restTemplate.postForObject(workspaceServiceUrl, request, ApiResponse.class);
            System.out.println("✅ Đã tạo Workspace cho Team: " + teamId);

        } catch (Exception e) {
            System.err.println("❌ Lỗi tạo Team Workspace: " + e.getMessage());
        }
    }
}