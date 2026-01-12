package com.collab.notificationservice.service;

import com.collab.notificationservice.dto.NotificationEventDTO;
import com.collab.notificationservice.entity.Notification;

import java.util.List;

public interface NotificationService {
    void handleNotification(NotificationEventDTO event);

    List<Notification> getNotificationsByUser(Long userId);
    void markAsRead(Long notificationId);
}

