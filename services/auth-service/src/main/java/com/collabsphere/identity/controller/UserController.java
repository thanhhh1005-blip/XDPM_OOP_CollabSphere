package com.collabsphere.identity.controller;

import com.collabsphere.identity.dto.request.*;
import com.collabsphere.identity.dto.response.ApiResponse;
import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// 👇 IMPORTS MỚI CHO UPLOAD FILE
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- CÁC API CŨ (GIỮ NGUYÊN) ---

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody UserCreationRequest request) {
        return ApiResponse.<User>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.<List<User>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/my-info")
    public ApiResponse<User> getMyInfo() {
        return ApiResponse.<User>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<User>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @PostMapping("/{userId}/change-password")
    public ApiResponse<String> changePassword(@PathVariable Long userId, @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ApiResponse.<String>builder()
                .result("Đổi mật khẩu thành công")
                .build();
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> toggleUserStatus(@PathVariable Long userId, @RequestBody UserStatusRequest request) {
        return ApiResponse.<User>builder()
                .result(userService.toggleUserStatus(userId, request.isActive()))
                .build();
    }

    // --- 👇 API MỚI: IMPORT EXCEL 👇 ---
    
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được import
    public ApiResponse<List<User>> importUsers(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<List<User>>builder()
                .result(userService.importUsers(file))
                .build();
    }
}