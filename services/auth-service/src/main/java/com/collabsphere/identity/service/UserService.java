package com.collabsphere.identity.service;

import com.collabsphere.identity.dto.request.UserCreationRequest;
import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.enums.Role;
import com.collabsphere.identity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder; // 👈 1. Import mới
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setActive(true); 

        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Xử lý Role
        if (request.getRole() != null) {
            try {
                user.setRole(Role.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(Role.STUDENT);
            }
        } else {
            user.setRole(Role.STUDENT);
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 👇 2. HÀM MỚI: Lấy thông tin người đang đăng nhập
    public User getMyInfo() {
        // Lấy username từ context (do Security Filter đã xác thực và lưu vào đây)
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        // Tìm user trong DB theo tên đó
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user;
    }
}