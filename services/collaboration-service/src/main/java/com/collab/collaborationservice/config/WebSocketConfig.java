package com.collab.collaborationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Điểm kết nối: ws://localhost:8080/ws-collab (qua gateway)
        registry.addEndpoint("/ws-collab").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic/collab"); // Kênh nhận dữ liệu
        registry.setApplicationDestinationPrefixes("/app"); // Kênh gửi dữ liệu
    }
}