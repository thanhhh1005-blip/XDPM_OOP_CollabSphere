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

  // ✅ ADD: mã đẹp để hiển thị (PR0001...)
  @Column(name = "project_code", nullable = false, unique = true, length = 20)
  private String projectCode;

  @Column(nullable = false)
  private String title;

  @Lob
  @Column(columnDefinition = "LONGTEXT")
  private String description;

  private String syllabusId;
  private String classId;
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
    if (status == null) status = ProjectStatus.DRAFT;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }
}
