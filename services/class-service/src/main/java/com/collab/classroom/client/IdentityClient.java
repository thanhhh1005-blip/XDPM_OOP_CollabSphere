package com.collab.classroom.client;

import com.collab.shared.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service", url = "http://localhost:8086/users")
public interface IdentityClient {

    @GetMapping("/{username}")
    UserDTO getUserByUsername(@PathVariable("username") String username);
}