package com.collab.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder; 
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String email;
    private String role;
}