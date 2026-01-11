package com.collab.projectservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "projects")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Project {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String title;

<<<<<<< HEAD
 @Lob
@Column(columnDefinition = "LONGTEXT")
private String description;

=======
  @Column(length = 2000)
  private String description;
>>>>>>> origin/main

  // Bổ sung: ID của đề cương môn học dùng để tạo dự án mẫu
  private String syllabusId; 

  // Bổ sung: ID của lớp học sau khi dự án được phê duyệt và giao lớp
  private String classId;

  // Bổ sung: ID của giảng viên tạo đề xuất dự án
  private String ownerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProjectStatus status;

  private Instant createdAt;
  private Instant updatedAt;

  @PrePersist
  void prePersist() {
    createdAt = Instant.now();
    updatedAt = createdAt;
    // Đảm bảo trạng thái mặc định luôn là DRAFT
    if (status == null) status = ProjectStatus.DRAFT; 
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }
}