package com.collabsphere.identity.controller;

import com.collabsphere.identity.entity.User;
import com.collabsphere.identity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 1. API Tạo User mới
    // POST http://localhost:8081/users
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // 2. API Lấy danh sách tất cả người dùng
    // GET http://localhost:8081/users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}