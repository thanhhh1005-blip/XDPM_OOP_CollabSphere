package com.collab.communicationservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Table(name = "active_meetings")
@Data
public class Meeting {
  @Id
  @Column(name = "room_id", length = 255)
  private String roomId; // Dùng chung ID với Team/Phòng chat
  private String hostName;
  private LocalDateTime startTime;
  private String password; // Lưu password cuộc họp nếu muốn
}