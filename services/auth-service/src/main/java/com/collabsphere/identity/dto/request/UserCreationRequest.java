package com.collabsphere.identity.dto.request;

public class UserCreationRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String role;
    private boolean isActive;

    // Constructor rỗng
    public UserCreationRequest() {
    }

    // Constructor đầy đủ
    public UserCreationRequest(String username, String password, String email, String fullName, String role, boolean isActive) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.isActive = isActive;
    }

    // --- Getter và Setter (Viết thủ công để tránh lỗi Lombok) ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}