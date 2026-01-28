package com.collab.classroom.client;

import com.collab.shared.dto.SubjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "subject-service", url = "http://localhost:8085")
public interface SubjectClient {

    // 1. Gọi API lấy theo ID
    @GetMapping("/api/v1/subjects/{id}")
    SubjectDTO getSubjectById(@PathVariable("id") Long id);

    // 2. Gọi API lấy theo CODE 
  
    @GetMapping("/api/v1/subjects/code/{code}")
    SubjectDTO getSubjectByCode(@PathVariable("code") String code);
}