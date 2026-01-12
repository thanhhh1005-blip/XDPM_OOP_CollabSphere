package com.collabsphere.identity.dto.request;

public class UserStatusRequest {
    private boolean active;

    public UserStatusRequest() {}

    public UserStatusRequest(boolean active) {
        this.active = active;
    }

    // Quan trọng: Getter cho boolean phải là isActive
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}