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

    // API lấy chi tiết theo ID
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable("id") Long id) { // Đã thêm ("id")
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(
            @PathVariable("id") Long id,
            @RequestBody SubjectDTO dto
    ) {
        return ResponseEntity.ok(subjectService.updateSubject(id, dto));
    }

    // --- BỔ SUNG: API XÓA ---
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable("id") Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok("Đã xóa môn học ID: " + id);
    }

    // API Import Excel
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importSubjects(@RequestParam("file") MultipartFile file) {
        subjectService.importSubjects(file);
        return ResponseEntity.ok("Import dữ liệu thành công!");
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SubjectDTO> getSubjectByCode(@PathVariable("code") String code) { // Nên thêm ("code") cho an toàn
        return ResponseEntity.ok(subjectService.getSubjectByCode(code));
    }
}