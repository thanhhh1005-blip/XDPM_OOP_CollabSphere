package com.collab.teamservice.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ClassServiceClient {

  private final RestTemplate restTemplate = new RestTemplate();

  // class-service chạy port 8084
  private static final String BASE = "http://localhost:8084/api/v1/classes";

  // ✅ class-service JSON: { "id": 1, "class_code": "CN23", "semester": "...", ... }
  public record ClassroomDTO(
      Long id,
      @JsonProperty("class_code") String classCode,
      String semester
  ) {}

  // ✅ match response: { "id":1, "classId":1, "studentId":"..." }
  public record ClassEnrollmentDTO(Long id, Long classId, String studentId) {}

  public List<ClassroomDTO> getAllClasses() {
    return restTemplate.exchange(
        BASE,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<ClassroomDTO>>() {}
    ).getBody();
  }

  public List<ClassEnrollmentDTO> getStudentsByClass(Long classId) {
    return restTemplate.exchange(
        BASE + "/" + classId + "/students",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<ClassEnrollmentDTO>>() {}
    ).getBody();
  }
}
