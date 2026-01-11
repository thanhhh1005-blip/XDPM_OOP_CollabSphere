package com.collab.workspaceservice.service;

import com.collab.shared.dto.UserDTO;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventListener {

    /**
     * Dùng cấu hình 'bindings' để: 
     * Nếu chưa có Queue, chưa có Exchange, chưa có Binding -> Tự tạo hết luôn!
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "user_workspace_queue", durable = "true"),
            exchange = @Exchange(value = "user_exchange", type = "topic"),
            key = "user_updated"
    ))
    public void handleUserSync(UserDTO userDto) {
        System.out.println(">>> WORKSPACE SERVICE: Nhận tin nhắn đồng bộ!");
        System.out.println("Đang xử lý User: " + userDto.getFullName());
        
        // Logic xử lý tiếp theo của em...
    }
}