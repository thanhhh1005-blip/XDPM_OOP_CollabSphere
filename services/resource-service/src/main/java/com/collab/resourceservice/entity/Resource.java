package com.collab.resourceservice.entity;

import com.collab.resourceservice.enums.ResourceScope;
import com.collab.resourceservice.enums.ResourceType;
import com.collab.resourceservice.enums.UserRole;
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

    @Column(nullable = false)
    private String fileName;       // Tên hiển thị (vd: Baitap.pdf)

    @Column(nullable = false, unique = true)
    private String storedFileName; // Tên lưu trên S3 (UUID)

    @Column(nullable = false)
    private String filePath;       // Đường dẫn URL S3

    @Column(nullable = false)
    private String contentType;    // Loại MIME (vd: application/pdf) - QUAN TRỌNG ĐỂ DOWNLOAD

    @Column(nullable = false)
    private Long fileSize;         // Kích thước (bytes)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;     // PDF, VIDEO...

    // --- QUAN TRỌNG: Định danh tài nguyên thuộc về đâu ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceScope scope;   // CLASS hoặc TEAM

    @Column(nullable = false)
    private String scopeId;        // ID của Lớp hoặc Nhóm tương ứng
    // -----------------------------------------------------

    @Column(nullable = false)
    private String uploadedBy;     // ID người up (User ID)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole uploaderRole; // Vai trò người up

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean deleted = false; // Xóa mềm

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.deleted == null) this.deleted = false;
    }
}