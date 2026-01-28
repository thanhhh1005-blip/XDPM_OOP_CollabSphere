package com.collab.teamservice.client;

import com.collab.shared.dto.ApiResponse;
import com.collab.shared.dto.ProjectDTO; // Đảm bảo bạn có DTO này trong shared
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

@Component
@RequiredArgsConstructor
public class ProjectServiceClient {
    private final RestTemplate restTemplate;
    
    private final String PROJECT_URL = "http://localhost:8081/api/projects"; 

    public ProjectDTO getProjectById(String projectId) {
        if (projectId == null || projectId.isEmpty()) return null;
        try {
            return restTemplate.exchange(
                    PROJECT_URL + "/" + projectId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<ProjectDTO>>() {}
            ).getBody().getResult();
        } catch (Exception e) {
            System.err.println("Lỗi lấy thông tin Project: " + e.getMessage());
            return null;
        }
    }
}