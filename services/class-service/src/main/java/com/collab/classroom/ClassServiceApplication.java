package com.collab.classroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // 1. Import Feign
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.collab.shared.config.SharedConfig;

@SpringBootApplication
@EnableFeignClients 
@ComponentScan(basePackages = {"com.collab.classroom", "com.collab.shared"})
@Import(SharedConfig.class)
public class ClassServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassServiceApplication.class, args);
    }

}