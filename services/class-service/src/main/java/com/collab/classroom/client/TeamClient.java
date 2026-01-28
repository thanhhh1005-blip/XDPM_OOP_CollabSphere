package com.collab.classroom.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name = tên service đăng ký trên Eureka
@FeignClient(name = "team-service", url = "http://localhost:8091/api/v1/teams")
public interface TeamClient {
    // API giả định bên team-service trả về tên nhóm
    @GetMapping("/{id}/name")
    String getTeamName(@PathVariable("id") String id);
}