package com.collab.projectservice.infra;

import com.collab.projectservice.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    
    // Tên Exchange phải khớp với cấu hình RabbitMQ của bạn
    private static final String EXCHANGE = "project.exchange";

    /**
     * Thông báo khi dự án được nộp duyệt
     */
    public void publishProjectSubmitted(Project project) {
        rabbitTemplate.convertAndSend(EXCHANGE, "project.submitted", project);
    }

    /**
     * Thông báo khi dự án được phê duyệt
     */
    public void publishProjectApproved(Project project) {
        rabbitTemplate.convertAndSend(EXCHANGE, "project.approved", project);
    }
}