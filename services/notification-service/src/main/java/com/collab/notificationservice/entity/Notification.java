package com.collab.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người nhận thông báo
    @Column(nullable = false)
    private Long userId;

    // Tiêu đề
    @Column(nullable = false)
    private String title;

    // Nội dung
    @Column(columnDefinition = "TEXT")
    private String content;

    // CHECKPOINT, EVALUATION, MILESTONE, REPORT...
    @Column(nullable = false)
    private String type;

    // Đã đọc chưa
    private Boolean isRead;

    private LocalDateTime createdAt;
}
