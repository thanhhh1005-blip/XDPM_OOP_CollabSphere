package com.collabsphere.identity.controller;

import com.collabsphere.identity.dto.request.*;
import com.collabsphere.identity.dto.response.ApiResponse;
import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
=======
// 👇 IMPORTS MỚI CHO UPLOAD FILE
import org.springframework.web.multipart.MultipartFile;
>>>>>>> origin/main
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

<<<<<<< HEAD
    // 1. Tạo User (POST /users)
=======
    // --- CÁC API CŨ (GIỮ NGUYÊN) ---

>>>>>>> origin/main
    @PostMapping
    public ApiResponse<User> createUser(@RequestBody UserCreationRequest request) {
        return ApiResponse.<User>builder()
                .result(userService.createUser(request))
                .build();
    }

<<<<<<< HEAD
    // 2. Lấy danh sách Users (GET /users)
=======
>>>>>>> origin/main
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.<List<User>>builder()
                .result(userService.getAllUsers())
                .build();
    }

<<<<<<< HEAD
    // 3. Lấy thông tin chính mình (GET /users/my-info)
=======
>>>>>>> origin/main
    @GetMapping("/my-info")
    public ApiResponse<User> getMyInfo() {
        return ApiResponse.<User>builder()
                .result(userService.getMyInfo())
                .build();
    }

<<<<<<< HEAD
    // 👇 4. API MỚI: Cập nhật thông tin (PUT /users/{userId})
=======
>>>>>>> origin/main
    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<User>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

<<<<<<< HEAD
    // 👇 5. API MỚI: Đổi mật khẩu (POST /users/{userId}/change-password)
=======
>>>>>>> origin/main
    @PostMapping("/{userId}/change-password")
    public ApiResponse<String> changePassword(@PathVariable Long userId, @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ApiResponse.<String>builder()
                .result("Đổi mật khẩu thành công")
                .build();
    }

<<<<<<< HEAD
    // 👇 6. API MỚI: Vô hiệu hóa/Kích hoạt tài khoản (PATCH /users/{userId}/status)
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được khóa
=======
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
>>>>>>> origin/main
    public ApiResponse<User> toggleUserStatus(@PathVariable Long userId, @RequestBody UserStatusRequest request) {
        return ApiResponse.<User>builder()
                .result(userService.toggleUserStatus(userId, request.isActive()))
                .build();
    }
<<<<<<< HEAD
=======

    // --- 👇 API MỚI: IMPORT EXCEL 👇 ---
    
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được import
    public ApiResponse<List<User>> importUsers(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<List<User>>builder()
                .result(userService.importUsers(file))
                .build();
    }
>>>>>>> origin/main
}