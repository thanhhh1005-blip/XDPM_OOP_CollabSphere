package com.collabsphere.identity.controller;

import com.collabsphere.identity.dto.request.*;
import com.collabsphere.identity.dto.response.ApiResponse;
import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Import cho Upload File

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- 1. Tạo User ---
    @PostMapping
    public ApiResponse<User> createUser(@RequestBody UserCreationRequest request) {
        return ApiResponse.<User>builder()
                .result(userService.createUser(request))
                .build();
    }

    // --- 2. Lấy danh sách Users ---
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.<List<User>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    // --- 3. Lấy thông tin chính mình ---
    @GetMapping("/my-info")
    public ApiResponse<User> getMyInfo() {
        return ApiResponse.<User>builder()
                .result(userService.getMyInfo())
                .build();
    }

    // --- 4. Cập nhật thông tin ---
    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<User>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    // --- 5. Đổi mật khẩu ---
    @PostMapping("/{userId}/change-password")
    public ApiResponse<String> changePassword(@PathVariable Long userId, @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ApiResponse.<String>builder()
                .result("Đổi mật khẩu thành công")
                .build();
    }

    // --- 6. Vô hiệu hóa/Kích hoạt ---
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> toggleUserStatus(@PathVariable Long userId, @RequestBody UserStatusRequest request) {
        return ApiResponse.<User>builder()
                .result(userService.toggleUserStatus(userId, request.isActive()))
                .build();
    }

    // --- 7. Lấy thông tin User (Sửa lỗi 405) ---
    @GetMapping("/{userId}")
    public ApiResponse<User> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<User>builder()
                .result(userService.getUser(userId))
                .build();
    }

    // --- 8. LẤY DANH SÁCH USER THEO ROLE (API MỚI) ---
    // Sửa lỗi: Gọi qua Service chứ không gọi trực tiếp Repository
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/role/{roleName}")
    public ApiResponse<List<User>> getUsersByRole(@PathVariable String roleName) {
        return ApiResponse.<List<User>>builder()
                // 👇 Gọi hàm mới trong Service (Đỡ phải import Role ở đây)
                .result(userService.getUsersByRole(roleName)) 
                .build();
    }

    // --- 9. IMPORT EXCEL ---
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<User>> importUsers(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<List<User>>builder()
                .result(userService.importUsers(file))
                .build();
    }
}