package com.collab.teamservice.api;

import com.collab.teamservice.client.ClassServiceClient;
import com.collab.teamservice.client.IdentityServiceClient;
import com.collab.teamservice.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/v1/teams/meta", "/teams/meta"})
@RequiredArgsConstructor
public class TeamMetaController {

  private final ClassServiceClient classClient;
  private final TeamRepository teamRepo;
  private final IdentityServiceClient identityClient;

  @GetMapping("/classes")
  public List<ClassOption> listClasses() {
    var classes = classClient.getAllClasses();
    if (classes == null) return List.of();

    return classes.stream()
        .map(c -> new ClassOption(c.id(), c.classCode(), c.semester()))
        .toList();
  }

  @GetMapping("/classes/{classId}/students")
  public List<StudentOption> listStudents(@PathVariable Long classId) {
    var students = classClient.getStudentsByClass(classId);
    if (students == null) return List.of();

    Set<String> usedLeaders = teamRepo.findByClassIdAndLeaderIdIsNotNull(classId)
        .stream()
        .map(t -> t.getLeaderId())
        .filter(x -> x != null && !x.isBlank())
        .collect(Collectors.toSet());

    return students.stream()
        .map(s -> {
          Long enrollId = s.id();       // id enrollment
          String sid = s.studentId();   // sv011...

          String fullName = null;
          try {
            fullName = identityClient.getFullNameByUsername(sid);
          } catch (Exception ignored) {}
          if (fullName == null || fullName.isBlank()) fullName = sid;

          boolean leaderUsed = usedLeaders.contains(sid);

          // ✅ ĐÚNG 4 FIELD, ĐÚNG THỨ TỰ
          return new StudentOption(enrollId, sid, fullName, leaderUsed);
        })
        .toList();
  }

  public record ClassOption(Long id, String classCode, String className) {}
  public record StudentOption(Long id, String studentId, String fullName, boolean leaderUsed) {}
}
