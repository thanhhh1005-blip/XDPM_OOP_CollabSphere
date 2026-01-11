package com.collabsphere.identity.service;

import com.collabsphere.identity.dto.request.PasswordChangeRequest;
import com.collabsphere.identity.dto.request.UserCreationRequest;
import com.collabsphere.identity.dto.request.UserUpdateRequest;
import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.enums.Role;
import com.collabsphere.identity.repository.UserRepository;
import com.collab.shared.dto.UserDTO;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


// 👇 IMPORTS MỚI CHO EXCEL
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.util.ArrayList;

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


    // --- CÁC HÀM CŨ (GIỮ NGUYÊN) ---


    public User createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

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
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void syncUserToOtherServices(User user) {
    // Tạo data cần gửi
    UserDTO event = new UserDTO(
        user.getId(), 
        user.getFullName(), 
        user.getAvatarUrl(), 
        user.getRole().name()
    );

    // Bắn tin nhắn đi
    rabbitTemplate.convertAndSend(
        "user_exchange", 
        "user_updated", 
        event
    );
    System.out.println("--- Đã gửi tin nhắn đồng bộ User qua RabbitMQ ---");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        return userRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    // 👇 LOGIC MỚI: Cập nhật thông tin

    public User updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());

        // Có thể thêm ngày sinh hoặc các trường khác nếu cần


        return userRepository.save(user);
    }


    // 👇 LOGIC MỚI: Đổi mật khẩu

    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));


        // Kiểm tra mật khẩu cũ

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }


        // Lưu mật khẩu mới

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    // 👇 LOGIC MỚI: Khóa/Mở khóa tài khoản

    public User toggleUserStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setActive(isActive);
        return userRepository.save(user);
    }



    // --- 👇 TÍNH NĂNG MỚI: IMPORT EXCEL 👇 ---

    public List<User> importUsers(MultipartFile file) {
        List<User> users = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            
            // Duyệt từ dòng 1 (bỏ qua Header ở dòng 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 1. Đọc dữ liệu thô
                String username = getCellValue(row, 0);
                String password = getCellValue(row, 1);
                String email = getCellValue(row, 2);
                String fullName = getCellValue(row, 3);
                String roleStr = getCellValue(row, 4).toUpperCase();

                // 2. Validate cơ bản (Bỏ qua nếu thiếu trường bắt buộc)
                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) continue;
                
                // 3. Kiểm tra trùng username trong DB
                if (userRepository.existsByUsername(username)) continue;

                // 4. Tạo User entity
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password)); // Mã hóa pass
                user.setEmail(email);
                
                // Xử lý Fullname (nếu rỗng thì lấy username)
                user.setFullName(fullName.isEmpty() ? username : fullName);

                // Xử lý Role
                try {
                    user.setRole(roleStr.isEmpty() ? Role.STUDENT : Role.valueOf(roleStr));
                } catch (Exception e) {
                    user.setRole(Role.STUDENT); // Fallback về Student nếu nhập sai
                }
                
                user.setActive(true);
                users.add(user);
            }

            // 5. Lưu batch vào Database
            return userRepository.saveAll(users);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
        }
    }

    // Hàm phụ trợ để lấy giá trị từ ô Excel an toàn
    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue()); // Ép kiểu số về int để bỏ .0
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }

}