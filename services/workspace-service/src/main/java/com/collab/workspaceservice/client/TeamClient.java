package com.collab.workspaceservice.client;

import com.collab.workspaceservice.dto.TeamResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// ⚠️ Lưu ý: Thay 8084 bằng port thực tế mà TeamService đang chạy
@FeignClient(name = "team-service", url = "http://localhost:8091/api/v1/teams")
public interface TeamClient {

    @GetMapping("/{id}")
    TeamResponse getTeamById(@PathVariable("id") String id);
}