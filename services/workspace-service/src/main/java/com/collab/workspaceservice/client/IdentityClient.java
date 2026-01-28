package com.collab.workspaceservice.client;

import com.collab.shared.dto.ApiResponse;
import com.collab.shared.dto.UserDTO; 
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-gateway-client", url = "http://localhost:8080/api/identity") 
public interface IdentityClient {

    @GetMapping("/users/username/{username}")
    ApiResponse<UserDTO> getUser(@PathVariable("username") String username);
}