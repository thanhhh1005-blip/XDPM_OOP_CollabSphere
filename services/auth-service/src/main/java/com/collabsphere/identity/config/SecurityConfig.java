package com.collabsphere.identity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // <--- Import thêm cái này nếu muốn chặn Method (Optional)
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

    // 👇 CẬP NHẬT DANH SÁCH NÀY 👇
    private final String[] PUBLIC_ENDPOINTS = {
            // "/users/**",
            "/auth/token",
            "/auth/introspect",
            "/auth/outbound/authentication",
            
            // 👇 THÊM DÒNG NÀY ĐỂ CLASS SERVICE GỌI ĐƯỢC (QUAN TRỌNG) 👇
            // "/api/users/**", 
            // -----------------------------------------------------------

            // Các đường dẫn cũ (Giữ nguyên nếu cần tương thích ngược)
            "/api/identity/users",
            "/api/identity/auth/token",
            "/api/identity/auth/introspect",
            "/error"
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
            request
                // 1. Cho phép các endpoint hoàn toàn public (Login, Auth...)
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                // 2. Cho phép ĐĂNG KÝ (POST /users) là public (Nếu app bạn cho phép tự đăng ký)
                .requestMatchers(HttpMethod.GET, "/users/{userId}").permitAll()

                // 3. Các API khác bắt buộc phải có Token (Authenticated)
                // Lúc này Token sẽ được phân tích, và @PreAuthorize bên Controller mới hoạt động đúng
                .anyRequest().authenticated()
        );

        httpSecurity.oauth2ResourceServer(oauth2 ->
            oauth2.jwt(jwtConfigurer ->
                jwtConfigurer.decoder(jwtDecoder())
                             .jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    // --- CÁC BEAN KHÁC GIỮ NGUYÊN (Không thay đổi) ---

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