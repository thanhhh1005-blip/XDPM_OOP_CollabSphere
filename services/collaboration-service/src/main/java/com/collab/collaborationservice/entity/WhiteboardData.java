package com.collab.collaborationservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "whiteboard_data")
@Data // <--- Quan trọng nhất để có hàm set
public class WhiteboardData {
    @Id
    private String teamId; 

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private LocalDateTime updatedAt; // <--- Thầy dùng updatedAt thay cho lastUpdated
}