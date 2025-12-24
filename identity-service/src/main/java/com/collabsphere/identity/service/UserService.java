package com.collabsphere.identity.service;

import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        // 1. Kiểm tra nếu username đã tồn tại thì báo lỗi
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        // 2. Mã hóa mật khẩu trước khi lưu (Bảo mật)
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. Lưu xuống Database
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}