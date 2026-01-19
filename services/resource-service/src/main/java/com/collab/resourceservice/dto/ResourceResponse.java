package com.collab.resourceservice.dto;

import com.collab.resourceservice.enums.ResourceScope;
import com.collab.resourceservice.enums.ResourceType;
import com.collab.resourceservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceResponse {
    private Long id;
    private String fileName;       // Tên hiển thị cho người dùng
    private String fileUrl;        // Link tải file (từ MinIO/S3)
    private String contentType;    // Loại file (MIME)
    private Long fileSize;         // Kích thước

    private ResourceType type;     // Enum: PDF, VIDEO...
    
    private ResourceScope scope;   // Enum: CLASS, TEAM
    private String scopeId;        // ID Lớp/Nhóm
    
    private String uploadedBy;     // Người up
    private UserRole uploaderRole; // Vai trò
    
    private LocalDateTime createdAt;
}