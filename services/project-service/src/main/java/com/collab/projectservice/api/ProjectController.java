package com.collab.projectservice.api;

import com.collab.projectservice.domain.Project;
import com.collab.projectservice.service.ProjectAppService;
import com.collab.projectservice.security.RoleGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/projects", "/api/v1/projects"})
@RequiredArgsConstructor
public class ProjectController {

  private final ProjectAppService service;

  /**
   * LECTURER: tạo project
   */
  @PostMapping
  public Project create(
      @RequestHeader(value = "X-ROLE", required = false) String role,
      @RequestBody CreateProjectReq req
  ) {
    RoleGuard.require(role, "LECTURER");
    return service.create(req.title(), req.description(), req.syllabusId());
  }

  /**
   * ALL ROLES: xem danh sách project
   * (Student chỉ xem, không thao tác)
   */
  @GetMapping
  public List<Project> getAll() {
    return service.getAll();
  }

  /**
   * ALL ROLES: xem chi tiết project
   * (sau này có thể giới hạn theo class/team)
   */
  @GetMapping("/{id}")
  public Project getById(
      @PathVariable("id") String id
  ) {
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
}
