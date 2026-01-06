package com.collab.classroom.client;

import com.collab.shared.dto.SubjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name: tên service cần gọi
// url: địa chỉ service đó (Subject-Service đang chạy ở 8081)
@FeignClient(name = "subject-service", url = "http://localhost:8081")
public interface SubjectClient {

    // 1. Gọi API lấy theo ID
    @GetMapping("/api/v1/subjects/{id}")
    SubjectDTO getSubjectById(@PathVariable("id") Long id);

    // 2. Gọi API lấy theo CODE (Sửa lại cho khớp với Controller bên kia)
    // Đường dẫn phải là /code/{code} và dùng @PathVariable
    @GetMapping("/api/v1/subjects/code/{code}")
    SubjectDTO getSubjectByCode(@PathVariable("code") String code);
}