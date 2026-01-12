package com.collab.notificationservice.service;

import com.collab.notificationservice.dto.EmailRequestDTO;

public interface EmailNotificationService {
    void sendEmail(EmailRequestDTO request);
}
