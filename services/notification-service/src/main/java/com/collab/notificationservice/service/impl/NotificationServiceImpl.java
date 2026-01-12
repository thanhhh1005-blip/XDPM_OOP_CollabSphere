package com.collab.notificationservice.service.impl;

import org.springframework.stereotype.Service;

import com.collab.notificationservice.service.NotificationService;
import com.collab.notificationservice.dto.NotificationEventDTO;
import com.collab.notificationservice.entity.Notification;
import com.collab.notificationservice.repository.NotificationRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public void handleNotification(NotificationEventDTO event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setTitle(event.getTitle());
        notification.setMessage(event.getContent()); // ✅ FIX: setContent -> setMessage
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }
}
