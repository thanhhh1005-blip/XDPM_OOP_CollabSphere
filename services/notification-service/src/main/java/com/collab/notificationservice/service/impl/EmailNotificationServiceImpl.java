package com.collab.notificationservice.service.impl;

import com.collab.notificationservice.dto.EmailRequestDTO;
import com.collab.notificationservice.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(EmailRequestDTO request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getTo());
        message.setSubject(request.getSubject());
        message.setText(request.getContent());
        mailSender.send(message);
    }
}
