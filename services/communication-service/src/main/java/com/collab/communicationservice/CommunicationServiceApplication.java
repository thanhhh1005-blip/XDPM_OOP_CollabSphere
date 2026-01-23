package com.collab.communicationservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.collab.communicationservice.repository.MeetingRepository;

@SpringBootApplication
public class CommunicationServiceApplication {
   public static void main(String[] args) {
        SpringApplication.run(CommunicationServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner init(MeetingRepository meetingRepository) {
        return args -> {
            meetingRepository.deleteAll();
            System.out.println(">>> Đã dọn dẹp tất cả phòng họp cũ!");
        };
    }
}
