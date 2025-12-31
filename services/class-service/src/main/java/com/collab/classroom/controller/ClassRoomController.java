package com.collab.classroom.controller;

import com.collab.classroom.service.ClassRoomService; // Import đúng Service mới
import com.collab.shared.dto.ClassroomDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class ClassRoomController { // Tên class đổi thành ClassRoomController

    private final ClassRoomService classRoomService; // Inject đúng Service mới

    @PostMapping
    public ResponseEntity<ClassroomDTO> createClass(@RequestBody ClassroomDTO dto) {
        return ResponseEntity.ok(classRoomService.createClass(dto));
    }

    @GetMapping
    public ResponseEntity<List<ClassroomDTO>> getAllClasses() {
        return ResponseEntity.ok(classRoomService.getAllClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDTO> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(classRoomService.getClassById(id));
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importClasses(@RequestParam("file") MultipartFile file) {
        classRoomService.importClasses(file);
        return ResponseEntity.ok("Import thành công!");
    }
}