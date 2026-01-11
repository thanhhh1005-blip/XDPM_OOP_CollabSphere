package com.collabsphere.identity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

<<<<<<< HEAD
    private final String[] PUBLIC_ENDPOINTS = {
            "/users", "/auth/token", "/auth/introspect",
            "/api/identity/users", "/api/identity/auth/token", "/api/identity/auth/introspect",
            "/error" // Giữ nguyên để hiển thị lỗi rõ ràng nếu có
=======
    // 👇 CẬP NHẬT DANH SÁCH NÀY 👇
    private final String[] PUBLIC_ENDPOINTS = {
            "/users", 
            "/auth/token", 
            "/auth/introspect",
            "/auth/outbound/authentication", // 👈 QUAN TRỌNG: Phải thêm dòng này để Login Google không bị chặn 401
            
            // Các đường dẫn cũ của bạn (Giữ nguyên)
            "/api/identity/users", 
            "/api/identity/auth/token", 
            "/api/identity/auth/introspect",
            "/error" 
>>>>>>> origin/main
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
                request
<<<<<<< HEAD
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated());
=======
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll() // Cho phép tất cả link trong mảng trên
                        .anyRequest().authenticated()); // Còn lại bắt buộc đăng nhập
>>>>>>> origin/main

        httpSecurity.oauth2ResourceServer(oauth2 ->
            oauth2.jwt(jwtConfigurer ->
                jwtConfigurer.decoder(jwtDecoder())
                             .jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

<<<<<<< HEAD

=======
>>>>>>> origin/main
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

<<<<<<< HEAD

=======
    // --- CÁC BEAN KHÁC GIỮ NGUYÊN ---
>>>>>>> origin/main

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                signerKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA512"
        );
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}