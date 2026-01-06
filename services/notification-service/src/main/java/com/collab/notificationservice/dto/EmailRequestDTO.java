package com.collab.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dùng để gửi email
 * (qua EmailService hoặc RabbitMQ event)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

    /**
     * Email người nhận
     */
    private String to;

    /**
     * Tiêu đề email
     */
    private String subject;

    /**
     * Nội dung email (HTML hoặc text)
     */
    private String content;
}
