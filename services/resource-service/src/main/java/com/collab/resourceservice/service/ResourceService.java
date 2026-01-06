package com.collab.resourceservice.service;
import com.collab.resourceservice.enums.ResourceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tên file gốc
    @Column(nullable = false)
    private String fileName;

    // Tên file lưu trên server (unique)
    @Column(nullable = false, unique = true)
    private String storedFileName;

    // Đường dẫn file local
    @Column(nullable = false)
    private String filePath;

    // Dung lượng file (bytes)
    private Long fileSize;

    // Loại file: PDF, DOCX, VIDEO,...
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    // Ai upload (userId hoặc username)
    @Column(nullable = false)
    private String uploadedBy;

    // Vai trò người upload: STAFF, LECTURER, STUDENT, ADMIN
    @Column(nullable = false)
    private String uploaderRole;

    // Thời gian upload
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Trạng thái xóa mềm
    @Column(nullable = false)
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.deleted == null) {
            this.deleted = false;
        }
    }
}
