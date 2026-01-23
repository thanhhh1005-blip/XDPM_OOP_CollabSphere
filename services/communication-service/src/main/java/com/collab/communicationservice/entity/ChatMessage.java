package com.collab.communicationservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private String senderName;
    @Column(columnDefinition = "TEXT") // Tên người gửi
    private String content;    // Nội dung chat
    private String roomId;       // ID phòng chat (hoặc ID nhóm)
    
    private LocalDateTime timestamp;
    private String type; 

}