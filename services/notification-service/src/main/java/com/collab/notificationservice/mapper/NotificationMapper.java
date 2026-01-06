package com.collab.notificationservice.mapper;

import com.collab.notificationservice.dto.NotificationResponseDTO;
import com.collab.notificationservice.entity.Notification;

public class NotificationMapper {

    public static NotificationResponseDTO toDTO(Notification entity) {
        return new NotificationResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.isRead(),
                entity.getCreatedAt()
        );
    }
}
