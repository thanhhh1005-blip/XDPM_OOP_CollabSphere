package com.collab.subject.controller;

import com.collab.shared.dto.SubjectDTO;
import com.collab.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    // API thêm mới
    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(subjectService.createSubject(dto));
    }

    // API lấy danh sách
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    // API lấy chi tiết theo ID (để Class-Service gọi)
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }
    
    // API Import Excel
    // Test bằng Postman: Chọn Body -> form-data -> key="file" (type File) -> Chọn file Excel
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importSubjects(@RequestParam("file") MultipartFile file) {
        subjectService.importSubjects(file);
        return ResponseEntity.ok("Import dữ liệu thành công!");
    }
    // Thêm vào SubjectController.java bên subject-service

    // API tìm môn học theo Mã (Để Class-Service gọi khi Import Excel)
    @GetMapping("/code/{code}")
    public ResponseEntity<SubjectDTO> getSubjectByCode(@PathVariable String code) {
        return ResponseEntity.ok(subjectService.getSubjectByCode(code));
    }
}