package com.collab.communicationservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Table(name = "active_meetings")
@Data
public class Meeting {
  @Id
  private Long roomId; // Dùng chung ID với Team/Phòng chat
    private String hostName;
    private LocalDateTime startTime;
    private String password; // Lưu password cuộc họp nếu muốn
}