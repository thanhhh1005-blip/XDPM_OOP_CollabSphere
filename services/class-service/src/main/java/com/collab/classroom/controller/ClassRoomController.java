package com.collab.classroom.controller;

import com.collab.classroom.entity.ClassEnrollment;
import com.collab.classroom.service.ClassRoomService;
import com.collab.shared.dto.ApiResponse;
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

    // --- 3. LẤY CHI TIẾT ---
    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDTO> getClassById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(classRoomService.getClassById(id));
    }

    // --- 4. CẬP NHẬT ---
    @PutMapping("/{id}")
    public ResponseEntity<ClassroomDTO> updateClass(
            @PathVariable("id") Long id,
            @RequestBody ClassroomDTO dto
    ) {
        return ResponseEntity.ok(classRoomService.updateClass(id, dto));
    }

    // --- 5. XÓA ---
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClass(@PathVariable("id") Long id) {
        classRoomService.deleteClass(id);
        return ResponseEntity.ok("Đã xóa lớp học có ID: " + id);
    }

    // --- 6. IMPORT EXCEL ---
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importClasses(@RequestParam("file") MultipartFile file) {
        classRoomService.importClasses(file);
        return ResponseEntity.ok("Import thành công!");
    }

    // --- 7. THÊM SINH VIÊN VÀO LỚP (ĐÃ SỬA LỖI 405) ---
    // Frontend gọi: POST /api/v1/classes/{id}/students/{studentId}
    @PostMapping("/{classId}/students/{studentId}")
    public ApiResponse<Void> addStudentToClass(
            @PathVariable("classId") Long classId,
            @PathVariable("studentId") String studentId // 👇 Đổi @RequestParam thành @PathVariable
    ) {
        classRoomService.addStudentToClass(classId, studentId);
        return new ApiResponse<Void>(1000, "Thêm sinh viên " + studentId + " thành công!", null);

    }

    // --- 8. LẤY DSSV CỦA LỚP ---
    @GetMapping("/{classId}/students")
    public ResponseEntity<List<ClassEnrollment>> getStudentsByClass(@PathVariable("classId") Long classId) {
        return ResponseEntity.ok(classRoomService.getStudentsByClass(classId));
    }

    // --- 9. XÓA SINH VIÊN KHỎI LỚP (ĐÃ CÓ) ---
    @DeleteMapping("/{classId}/students/{studentId}")
    public ApiResponse<Void> removeStudentFromClass(@PathVariable Long classId, @PathVariable String studentId) {
        classRoomService.removeStudentFromClass(classId, studentId);
        return new ApiResponse<Void>(1000, "Xóa sinh viên thành công", null);
    }
}