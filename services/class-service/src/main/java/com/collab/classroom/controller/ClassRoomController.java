package com.collab.classroom.controller;

import com.collab.classroom.service.ClassRoomService;
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
public class ClassRoomController {

    private final ClassRoomService classRoomService;

    @PostMapping
    public ResponseEntity<ClassroomDTO> createClass(@RequestBody ClassroomDTO dto) {
        return ResponseEntity.ok(classRoomService.createClass(dto));
    }

    @GetMapping
    public ResponseEntity<List<ClassroomDTO>> getAllClasses() {
        return ResponseEntity.ok(classRoomService.getAllClasses());
    }

    @GetMapping("/{id}")
public ResponseEntity<ClassroomDTO> getClassById(@PathVariable("id") Long id) { // <--- Thêm ("id") vào đây
    return ResponseEntity.ok(classRoomService.getClassById(id));
}

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importClasses(@RequestParam("file") MultipartFile file) {
        classRoomService.importClasses(file);
        return ResponseEntity.ok("Import thành công!");
    }
    @PostMapping("/{classId}/students")
    public ResponseEntity<String> addStudentToClass(
            @PathVariable("classId") Long classId,       
            @RequestParam("studentId") String studentId  
    ) {     
        classRoomService.addStudentToClass(classId, studentId);
        return ResponseEntity.ok("Đã thêm sinh viên " + studentId + " vào lớp thành công!");
    }
}