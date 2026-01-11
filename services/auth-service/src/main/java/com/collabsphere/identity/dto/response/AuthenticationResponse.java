package com.collabsphere.identity.dto.response;

import com.collab.shared.dto.UserDTO;

import lombok.*;
@Data // Tự tạo Getter, Setter, toString...
@Builder // Tự tạo hàm builder()
@NoArgsConstructor // Tự tạo Constructor rỗng
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private boolean authenticated;
    private UserDTO user;

    public AuthenticationResponse(String token, boolean authenticated) {
        this.token = token;
        this.authenticated = authenticated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}