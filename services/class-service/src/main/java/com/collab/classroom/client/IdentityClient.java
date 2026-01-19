package com.collab.classroom.client;

import com.collab.shared.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 👇 QUAN TRỌNG: Đã đổi port thành 8086
@FeignClient(name = "identity-service", url = "http://localhost:8086/users")
public interface IdentityClient {

    // Gọi API: GET http://localhost:8086/users/{username}
    @GetMapping("/{username}")
    UserDTO getUserByUsername(@PathVariable("username") String username);
}