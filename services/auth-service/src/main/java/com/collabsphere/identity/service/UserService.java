package com.collabsphere.identity.service;

import com.collabsphere.identity.dto.request.UserCreationRequest;
import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.enums.Role;
import com.collabsphere.identity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        // Xử lý Role: Dùng đúng Enum của dự án (STUDENT, LECTURER,...)
        if (request.getRole() != null) {
            try {
                // Chuyển chuỗi input thành Enum (VD: "LECTURER" -> Role.LECTURER)
                user.setRole(Role.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Nếu gửi role sai bét (VD: "ABC"), mặc định gán là STUDENT
                user.setRole(Role.STUDENT);
            }
        } else {
            // Nếu không gửi role, mặc định là STUDENT
            user.setRole(Role.STUDENT);
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}