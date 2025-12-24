package com.collabsphere.identity.dto;

public class AuthenticationRequest {
    private String username;
    private String password;

    // 1. Constructor mặc định
    public AuthenticationRequest() {}

    // 2. Constructor đầy đủ
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // 3. Getters và Setters thủ công (để sửa lỗi getUsername)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}