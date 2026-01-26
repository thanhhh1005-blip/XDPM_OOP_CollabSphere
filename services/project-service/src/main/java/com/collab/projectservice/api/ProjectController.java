package com.collab.projectservice.api;

import com.collab.projectservice.domain.Project;
import com.collab.projectservice.domain.ProjectStatus;
import com.collab.projectservice.service.ProjectAppService;
import com.collab.projectservice.security.RoleGuard;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.collab.projectservice.repo.ProjectRepository;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping({"/projects", "/api/v1/projects"})
@RequiredArgsConstructor
public class ProjectController {

  private final ProjectAppService service;
  private final ProjectRepository repo;
  /**
   * LECTURER: tạo project
   */
  @PostMapping
  public Project create(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @Valid  @RequestBody CreateProjectReq req
  ) {
    RoleGuard.require(role, "LECTURER");
    // Sửa dòng này: Truyền syllabusContent thay vì syllabusId
    return service.create(req.title(), req.description(), req.syllabusContent());
  }

  /**
   * ALL ROLES: xem danh sách project
   * (Student chỉ xem, không thao tác)
   */
  // ... import ...

@GetMapping
public List<Project> getAll(@RequestParam(value = "status", required = false) String statusStr) {
    // Nếu Client gửi ?status=APPROVED thì lọc, không thì lấy hết
    if (statusStr != null && !statusStr.isEmpty()) {
        try {
            ProjectStatus status = ProjectStatus.valueOf(statusStr.toUpperCase());
            return repo.findByStatus(status);
        } catch (IllegalArgumentException e) {
            // Nếu status sai thì trả về rỗng hoặc lỗi tuỳ bạn
            return List.of();
        }
    }
    return service.getAll();
}

  /**
   * ALL ROLES: xem chi tiết project
   * (sau này có thể giới hạn theo class/team)
   */
  @GetMapping("/{id}")
  public Project getById(@PathVariable("id") String id) {
    return service.getById(id);
  }

  /**
   * LECTURER: submit project để Head duyệt
   */
  @PostMapping("/{id}/submit")
  public Project submit(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @PathVariable("id") String id
  ) {
    RoleGuard.require(role, "LECTURER");
    return service.submit(id);
  }

  /**
   * HEAD_DEPARTMENT: approve project
   */
  @PostMapping("/{id}/approve")
  public Project approve(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @PathVariable("id") String id
  ) {
    RoleGuard.require(role, "HEAD_DEPARTMENT");
    return service.approve(id);
  }

  /**
   * HEAD_DEPARTMENT: deny project
   */
  @PostMapping("/{id}/deny")
  public Project deny(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @PathVariable("id") String id
  ) {
    RoleGuard.require(role, "HEAD_DEPARTMENT");
    return service.deny(id);
  }

  /**
   * HEAD_DEPARTMENT: assign project cho lớp
   */
  @PostMapping("/{id}/assign/{classId}")
  public Project assignToClass(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @PathVariable("id") String id,
      @PathVariable("classId") String classId
  ) {
    RoleGuard.require(role, "HEAD_DEPARTMENT");
    return service.assignToClass(id, classId);
  }

  @PostMapping(value = "/import-syllabus", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String importSyllabus(@RequestParam("file") MultipartFile file) {
        return service.extractSyllabusFromExcel(file);
    }
}
