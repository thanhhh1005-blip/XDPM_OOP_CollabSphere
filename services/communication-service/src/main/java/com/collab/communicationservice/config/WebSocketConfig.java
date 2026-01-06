package com.collab.communicationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đây là cái "ổ cắm" để Frontend cắm vào: ws://localhost:8082/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Cho phép ReactJS gọi vào
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Tin nhắn từ Server gửi xuống Client sẽ bắt đầu bằng /topic
        registry.enableSimpleBroker("/topic");
        // Tin nhắn từ Client gửi lên Server sẽ bắt đầu bằng /app
        registry.setApplicationDestinationPrefixes("/app");
    }
}