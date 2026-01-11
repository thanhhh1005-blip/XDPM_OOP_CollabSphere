package com.collab.projectservice.api;

import com.collab.projectservice.domain.Project;
import com.collab.projectservice.service.ProjectAppService;
<<<<<<< HEAD
import com.collab.projectservice.security.RoleGuard;
=======
>>>>>>> origin/main
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
<<<<<<< HEAD
@RequestMapping({"/projects", "/api/v1/projects"})
@RequiredArgsConstructor
=======
@RequestMapping("/projects")
@RequiredArgsConstructor
// ĐÃ XÓA @CrossOrigin vì đã có cấu hình tại Gateway
>>>>>>> origin/main
public class ProjectController {

  private final ProjectAppService service;

<<<<<<< HEAD
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
=======
  @PostMapping
  public Project create(@RequestBody CreateProjectReq req) {
    return service.create(req.title(), req.description(), req.syllabusId());
  }

>>>>>>> origin/main
  @GetMapping
  public List<Project> getAll() {
    return service.getAll();
  }

<<<<<<< HEAD
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
=======
  @GetMapping("/{id}")
  public Project getById(@PathVariable("id") String id) {
    return service.getById(id);
  }

  @PostMapping("/{id}/submit")
  public Project submit(@PathVariable("id") String id) {
    return service.submit(id);
  }

  @PostMapping("/{id}/approve")
  public Project approve(@PathVariable("id") String id) {
    return service.approve(id);
  }

  @PostMapping("/{id}/deny")
  public Project deny(@PathVariable("id") String id) {
    return service.deny(id);
  }

  @PostMapping("/{id}/assign/{classId}")
  public Project assignToClass(
      @PathVariable("id") String id,
      @PathVariable("classId") String classId
  ) {
>>>>>>> origin/main
    return service.assignToClass(id, classId);
  }
}
