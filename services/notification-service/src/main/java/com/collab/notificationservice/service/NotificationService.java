package com.collab.notificationservice.service;

import com.collab.notificationservice.dto.NotificationEventDTO;
import com.collab.notificationservice.dto.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {

    void handleNotification(NotificationEventDTO event);

    List<NotificationResponseDTO> getUserNotifications(Long userId);
}
