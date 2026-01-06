package com.collab.notificationservice.service.impl;

import com.collab.notificationservice.dto.*;
import com.collab.notificationservice.entity.Notification;
import com.collab.notificationservice.mapper.NotificationMapper;
import com.collab.notificationservice.repository.NotificationRepository;
import com.collab.notificationservice.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final EmailNotificationService emailService;
    private final WebSocketPushService webSocketPushService;

    @Override
    public void handleNotification(NotificationEventDTO event) {

        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .title(event.getTitle())
                .content(event.getContent())
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = repository.save(notification);

        if (event.isSendEmail()) {
            emailService.sendEmail(
                    new EmailRequestDTO(
                            event.getEmail(),
                            event.getTitle(),
                            event.getContent()
                    )
            );
        }

        if (event.isSendWebSocket()) {
            webSocketPushService.push(saved);
        }
    }

    @Override
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationMapper::toDTO)
                .toList();
    }
}
