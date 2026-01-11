package com.collab.resourceservice.entity;

<<<<<<< HEAD
import com.collab.resourceservice.enums.ResourceType; 
=======
import com.collab.resourceservice.enums.ResourceType;
>>>>>>> origin/main
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

<<<<<<< HEAD
    // Tên file gốc
    @Column(nullable = false)
    private String fileName;

    // Tên file lưu trên server
    @Column(nullable = false, unique = true)
    private String storedFileName;

    // Đường dẫn file local
    @Column(nullable = false)
    private String filePath;

    // Dung lượng file (bytes)
    @Column(nullable = false)
    private Long fileSize;

    // Loại file: PDF, DOCX, VIDEO,...
=======
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false, unique = true)
    private String storedFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

>>>>>>> origin/main
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

<<<<<<< HEAD
    // Ai upload (userId hoặc username)
    @Column(nullable = false)
    private String uploadedBy;

    // Vai trò người upload
    @Column(nullable = false)
    private String uploaderRole; // STAFF, LECTURER, STUDENT, ADMIN...

    // Thời gian upload
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Trạng thái xóa mềm
=======
    @Column(nullable = false)
    private String uploadedBy;

    @Column(nullable = false)
    private String uploaderRole;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

>>>>>>> origin/main
    @Column(nullable = false)
    private Boolean deleted = false;

    @PrePersist
<<<<<<< HEAD
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
=======
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
>>>>>>> origin/main
