package com.collab.classroom.controller;

import com.collab.classroom.entity.ClassEnrollment;
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

    // --- 1. TẠO MỚI ---
    @PostMapping
    public ResponseEntity<ClassroomDTO> createClass(@RequestBody ClassroomDTO dto) {
        return ResponseEntity.ok(classRoomService.createClass(dto));
    }

    // --- 2. LẤY DANH SÁCH ---
    @GetMapping
    public ResponseEntity<List<ClassroomDTO>> getAllClasses() {
        return ResponseEntity.ok(classRoomService.getAllClasses());
    }

    // --- 3. LẤY CHI TIẾT (Đã fix lỗi tham số) ---
    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDTO> getClassById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(classRoomService.getClassById(id));
    }

    // --- 4. CẬP NHẬT (MỚI THÊM) ---
    @PutMapping("/{id}")
    public ResponseEntity<ClassroomDTO> updateClass(
            @PathVariable("id") Long id, // Quan trọng: phải có ("id")
            @RequestBody ClassroomDTO dto
    ) {
        return ResponseEntity.ok(classRoomService.updateClass(id, dto));
    }

    // --- 5. XÓA (MỚI THÊM) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClass(@PathVariable("id") Long id) { // Quan trọng: phải có ("id")
        classRoomService.deleteClass(id);
        return ResponseEntity.ok("Đã xóa lớp học có ID: " + id);
    }

    // --- 6. IMPORT EXCEL ---
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importClasses(@RequestParam("file") MultipartFile file) {
        classRoomService.importClasses(file);
        return ResponseEntity.ok("Import thành công!");
    }

    // --- 7. THÊM SINH VIÊN VÀO LỚP ---
    @PostMapping("/{classId}/students")
    public ResponseEntity<String> addStudentToClass(
            @PathVariable("classId") Long classId,
            @RequestParam("studentId") String studentId
    ) {
        classRoomService.addStudentToClass(classId, studentId);
        return ResponseEntity.ok("Đã thêm sinh viên " + studentId + " vào lớp thành công!");
    }

    // --- 8. LẤY DSSV CỦA LỚP ---
    @GetMapping("/{classId}/students")
    public ResponseEntity<List<ClassEnrollment>> getStudentsByClass(@PathVariable("classId") Long classId) {
        return ResponseEntity.ok(classRoomService.getStudentsByClass(classId));
    }
}