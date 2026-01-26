package com.collab.classroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // 1. Import Feign
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.collab.shared.config.SharedConfig;

@SpringBootApplication
@EnableFeignClients // 2. QUAN TRỌNG: Bắt buộc phải có dòng này để kích hoạt SubjectClient
@ComponentScan(basePackages = {"com.collab.classroom", "com.collab.shared"}) // 3. Quét thêm module shared cho chắc chắn
@Import(SharedConfig.class)
public class ClassServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassServiceApplication.class, args);
    }

    // Bạn có thể xóa Bean RestTemplate nếu không dùng, vì mình đã dùng Feign Client rồi
}