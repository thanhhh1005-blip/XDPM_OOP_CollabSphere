package com.collab.notificationservice.dto;

import lombok.Data;

@Data
public class NotificationEventDTO {

    private Long userId;
    private String title;
    private String content;
    private String type;
}
