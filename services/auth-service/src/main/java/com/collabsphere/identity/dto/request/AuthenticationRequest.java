package com.collabsphere.identity.dto.request;

public class AuthenticationRequest {
    private String username;
    private String password;

    // 1. Constructor rỗng
    public AuthenticationRequest() {
    }

    // 2. Constructor đầy đủ
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // 3. Getter và Setter (Bắt buộc phải có để sửa lỗi)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}