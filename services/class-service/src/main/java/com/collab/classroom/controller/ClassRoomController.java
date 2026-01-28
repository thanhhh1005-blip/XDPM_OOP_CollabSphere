package com.collab.classroom.controller;

import com.collab.classroom.entity.ClassEnrollment;
import com.collab.classroom.service.ClassRoomService;
import com.collab.shared.dto.ApiResponse;
import com.collab.shared.dto.ClassroomDTO;
import com.collab.shared.dto.ClassMemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Slf4j
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
    public ApiResponse<Void> removeStudentFromClass(@PathVariable("classId") Long classId, @PathVariable("studentId") String studentId) {
        classRoomService.removeStudentFromClass(classId, studentId);
        return new ApiResponse<Void>(1000, "Xóa sinh viên thành công", null);
    }

    // --- 10. API LẤY LỚP CỦA TÔI (Dành cho GV) ---
    // Frontend gọi: GET /api/v1/classes/teacher/{username}
    @GetMapping("/teacher/{username}")
    public ResponseEntity<List<ClassroomDTO>> getMyClasses(@PathVariable("username") String username) {
        return ResponseEntity.ok(classRoomService.getClassesByTeacher(username));
    }

    // GET /api/v1/classes/student/{studentId}
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ClassroomDTO>> getStudentClasses(@PathVariable("studentId") String studentId) {
    return ResponseEntity.ok(classRoomService.getClassesForStudent(studentId));
    }

    // --- Sửa tại ClassRoomController.java ---

    @PostMapping("/{classId}/bulk-enroll") // 👈 Đổi từ students/bulk thành bulk-enroll
    public ApiResponse<Void> addStudents(@PathVariable("classId") Long classId, @RequestBody List<String> studentIds) {
        classRoomService.addStudentsToClass(classId, studentIds);
        return new ApiResponse<>(1000, "Đã thêm sinh viên vào lớp", null); // 👈 Sửa thành true
    }

    @GetMapping("/teacher/{username}/ids")
public List<Long> getClassIds(@PathVariable("username") String username) {
    return classRoomService.getClassesByTeacher(username)
            .stream()
            .map(dto -> dto.getId())
            .toList();
}

    @GetMapping("/{classId}/workspace-members")
    public ResponseEntity<List<ClassMemberDTO>> getWorkspaceMembers(@PathVariable("classId") Long classId) {
        // 1. Lấy thông tin lớp để tìm Giảng viên
        ClassroomDTO classroom = classRoomService.getClassById(classId);
        
        List<ClassMemberDTO> members = new ArrayList<>();

        // 2. Thêm Giảng viên vào list 

        members.add(new ClassMemberDTO(classroom.getTeacherId(), "TEACHER", "Giảng viên (" + classroom.getTeacherId() + ")"));

        // 3. Lấy danh sách Sinh viên
        List<ClassEnrollment> students = classRoomService.getStudentsByClass(classId);
        for (ClassEnrollment enrollment : students) {
             members.add(new ClassMemberDTO(enrollment.getStudentId(), "STUDENT", "Sinh viên (" + enrollment.getStudentId() + ")"));
        }

        return ResponseEntity.ok(members);
    }
    @GetMapping("/my-list")
    public ResponseEntity<List<ClassroomDTO>> getMyClassList(
            @RequestHeader(value = "X-USER-ID", required = false) String userId,
            @RequestHeader(value = "X-ROLE", required = false) String role
    ) {
        log.info("🔍 /my-list được gọi - Role: {}, UserId: {}", role, userId);
        
        if (userId == null || userId.isBlank()) {
            log.error("❌ X-USER-ID bị thiếu hoặc rỗng");
            return ResponseEntity.badRequest().build();
        }
        
        if (role == null || role.isBlank()) {
            log.error("❌ X-ROLE bị thiếu hoặc rỗng");
            return ResponseEntity.badRequest().build();
        }
        
        if ("LECTURER".equalsIgnoreCase(role)) {
            List<ClassroomDTO> classes = classRoomService.getClassesByTeacher(userId);
            log.info("✅ Tìm thấy {} lớp cho giảng viên {}", classes.size(), userId);
            return ResponseEntity.ok(classes);
        }
        
        if ("ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.ok(classRoomService.getAllClasses());
        }

        log.warn("⚠️ Role không được hỗ trợ: {}", role);
        return ResponseEntity.ok(List.of());
    }
    
}