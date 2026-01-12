package com.collab.notificationservice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private String title;
    private String content;
    private boolean read;
    private LocalDateTime createdAt;
}
