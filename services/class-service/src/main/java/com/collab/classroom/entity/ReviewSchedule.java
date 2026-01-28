package com.collab.classroom.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ngày giờ bắt đầu - kết thúc
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    // Địa điểm (Online Link hoặc Phòng họp)
    private String location;
    private String title;
    @Column(nullable = false)
    private String type;

    // ID tham chiếu
    @Column(name = "class_id", nullable = false)
    private Long classId; 

    @Column(name = "team_id")
    private String teamId; 
}