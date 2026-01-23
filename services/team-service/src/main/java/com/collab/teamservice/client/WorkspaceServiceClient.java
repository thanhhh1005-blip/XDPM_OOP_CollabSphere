package com.collab.teamservice.client;

import com.collab.shared.dto.ApiResponse; // Dòng này sẽ hết đỏ sau khi làm PHẦN 1
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WorkspaceServiceClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE = "http://localhost:8080/api/workspace/workspaces";

    public void createWorkspaceForTeam(String teamId) {
        String url = BASE + "/team/" + teamId;
        try {
            // Gọi POST sang workspace-service
            restTemplate.postForObject(url, null, ApiResponse.class);
        } catch (Exception e) {
            System.err.println("Lỗi kích hoạt Workspace: " + e.getMessage());
        }
    }
}