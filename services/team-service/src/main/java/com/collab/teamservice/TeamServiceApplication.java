package com.collab.teamservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.collab.shared.config.SharedConfig;

@SpringBootApplication
@Import(SharedConfig.class)
public class TeamServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(TeamServiceApplication.class, args);
  }
}
