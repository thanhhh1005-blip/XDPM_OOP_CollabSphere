package com.collab.teamservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IdentityServiceClient {

  private final RestTemplate restTemplate = new RestTemplate();

  private static final String BASE = "http://localhost:8080/api/v1/users";

  public record UserDTO(String username, String fullName) {}

  public String getFullNameByUsername(String username) {
    try {
      UserDTO user = restTemplate.getForObject(BASE + "/" + username, UserDTO.class);
      return user != null ? user.fullName() : null;
    } catch (Exception e) {
      return null;
    }
  }
}
