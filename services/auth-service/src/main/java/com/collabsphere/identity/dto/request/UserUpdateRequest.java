package com.collabsphere.identity.dto.request;

import java.time.LocalDate;

public class UserUpdateRequest {
    private String username;
    private String fullName;
    private String email;
    private LocalDate dob;

    // 1. Constructors
    public UserUpdateRequest() {}

    public UserUpdateRequest(String username, String fullName, String email, LocalDate dob) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.dob = dob;
    }

    // 2. Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
}