package com.collab.teamservice.api;

import com.collab.teamservice.client.ClassServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/teams/meta", "/teams/meta"}) // ✅ để gateway rewrite cũng chạy
@RequiredArgsConstructor
public class TeamMetaController {

  private final ClassServiceClient classClient;

  // ✅ Lấy danh sách lớp: trả cả id + classCode + "tên lớp" (tạm lấy semester)
  @GetMapping("/classes")
  public List<ClassOption> listClasses() {
    var classes = classClient.getAllClasses();
    if (classes == null) return List.of();

    return classes.stream()
        .map(c -> new ClassOption(
    c.id(),
    c.classCode(),
    c.semester()
))
.toList();

  }

  // ✅ Lấy DS sinh viên theo lớp (để FE chọn trưởng nhóm)
  @GetMapping("/classes/{classId}/students")
  public List<StudentOption> listStudents(@PathVariable Long classId) {
    var students = classClient.getStudentsByClass(classId);
    if (students == null) return List.of();

    return students.stream()
        .map(s -> new StudentOption(s.studentId()))
        .toList();
  }

  public record ClassOption(Long id, String classCode, String className) {}
  public record StudentOption(String studentId) {}
}
