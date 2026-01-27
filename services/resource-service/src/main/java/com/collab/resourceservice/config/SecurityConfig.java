package com.collab.resourceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF để test API bằng Postman được
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // ⚠️ TẠM THỜI: Cho phép tất cả request (để App chạy lên đã)
            );
        
        return http.build();
    }
}