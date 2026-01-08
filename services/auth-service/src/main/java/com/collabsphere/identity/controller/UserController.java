package com.collabsphere.identity.controller;

import com.collabsphere.identity.dto.request.UserCreationRequest;
import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody UserCreationRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") 
    public List<User> getAllUsers() {
        // Log debug
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User đang gọi: " + authentication.getName());
        System.out.println("Quyền hạn (Roles): " + authentication.getAuthorities());

        return userService.getAllUsers();
    }

    // 👇 3. API MỚI: Lấy thông tin chính mình
    // Không cần @PreAuthorize vì ai đăng nhập rồi cũng được gọi
    @GetMapping("/my-info")
    public User getMyInfo() {
        return userService.getMyInfo();
    }
}