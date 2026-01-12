package com.collab.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Notification Service Application
 *
 * Chức năng:
 * - Nhận event từ RabbitMQ
 * - Gửi Email
 * - Push WebSocket real-time
 * - Lưu Notification vào Database
 */
@SpringBootApplication
@EnableAsync
@EntityScan(basePackages = "com.collab.notificationservice.entity")
@EnableJpaRepositories(basePackages = "com.collab.notificationservice.repository")
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
