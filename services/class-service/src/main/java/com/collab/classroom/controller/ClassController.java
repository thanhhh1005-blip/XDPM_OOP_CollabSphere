package com.collab.classroom.controller;

import com.collab.classroom.entity.ClassRoom;
import com.collab.classroom.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService service;

    @PostMapping
    public ClassRoom create(@RequestBody ClassRoom classRoom) {
        return service.createClass(classRoom);
    }

    @GetMapping
    public List<ClassRoom> getAll() {
        return service.getAllClasses();
    }
}