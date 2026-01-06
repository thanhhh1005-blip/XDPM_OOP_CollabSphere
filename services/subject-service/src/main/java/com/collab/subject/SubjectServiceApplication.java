package com.collab.subject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Scan cả package 'com.collab.shared' để nhận diện DTO nếu có config Bean bên đó
@ComponentScan(basePackages = {"com.collab.subject", "com.collab.shared"}) 
public class SubjectServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubjectServiceApplication.class, args);
    }
}