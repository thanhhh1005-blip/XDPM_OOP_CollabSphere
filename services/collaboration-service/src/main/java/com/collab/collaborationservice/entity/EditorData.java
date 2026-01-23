package com.collab.collaborationservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "editor_data")
@Data
public class EditorData {
    @Id
    private String teamId;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private LocalDateTime updatedAt;
}