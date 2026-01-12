package com.collab.notificationservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDTO {

    private Long userId;
    private String title;
    private String content;
    private boolean sendEmail;
    private boolean sendWebSocket;
    private String email;
}
