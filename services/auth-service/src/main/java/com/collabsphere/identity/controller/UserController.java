package com.collabsphere.identity.controller;

import com.collabsphere.identity.dto.request.UserCreationRequest; // 1. Thêm dòng import này
import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users") // Lưu ý: Gateway cấu hình là /identity/**, nên endpoint thực tế sẽ là /identity/users
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    // 2. Sửa tham số: Nhận UserCreationRequest thay vì User
    public User createUser(@RequestBody UserCreationRequest request) {
        // 3. Gọi service với tham số mới
        return userService.createUser(request);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}