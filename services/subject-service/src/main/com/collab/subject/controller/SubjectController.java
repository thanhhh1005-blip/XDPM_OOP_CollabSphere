package com.collab.subject.controller;

import com.collab.subject.entity.Subject;
import com.collab.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService service;

    // API tạo môn học mới: POST http://localhost:8081/api/subjects
    @PostMapping
    public Subject create(@RequestBody Subject subject) {
        return service.createSubject(subject);
    }

    // API lấy danh sách môn học: GET http://localhost:8081/api/subjects
    @GetMapping
    public List<Subject> getAll() {
        return service.getAllSubjects();
    }

    // API lấy chi tiết 1 môn: GET http://localhost:8081/api/subjects/{id}
    @GetMapping("/{id}")
    public Subject getById(@PathVariable Long id) {
        return service.getSubjectById(id);
    }
}