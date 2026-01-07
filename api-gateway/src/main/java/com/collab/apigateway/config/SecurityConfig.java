package com.collab.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity 
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            // Tắt CSRF (Thủ phạm gây lỗi 403)
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            
            // Tạm thời cho phép tất cả request đi qua (Gateway chỉ đóng vai trò Router)
            // Sau này ta sẽ chặn lại để check Token sau
            .authorizeExchange(exchange -> exchange
                .anyExchange().permitAll()
            );

        return http.build();
    }
}