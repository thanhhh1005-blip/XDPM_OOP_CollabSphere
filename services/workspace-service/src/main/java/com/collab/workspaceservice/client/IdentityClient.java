package com.collab.workspaceservice.client;

import com.collab.shared.dto.ApiResponse;
import com.collab.shared.dto.UserDTO; // Tạo DTO này ở bước dưới
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name: tên service identity, url: địa chỉ chạy identity service
@FeignClient(name = "identity-gateway-client", url = "http://localhost:8080/api/identity") 
public interface IdentityClient {

    // Khi gọi hàm này -> Full URL: http://localhost:8080/api/identity/users/username/student2
    // Qua Gateway cắt 2 cái đầu -> Còn: /users/username/student2 -> Vào đúng Controller
    @GetMapping("/users/username/{username}")
    ApiResponse<UserDTO> getUser(@PathVariable("username") String username);
}