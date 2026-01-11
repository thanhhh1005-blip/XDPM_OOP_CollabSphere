package com.collab.projectservice.api;

import com.collab.projectservice.domain.Project;
import com.collab.projectservice.service.ProjectAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
// ĐÃ XÓA @CrossOrigin vì đã có cấu hình tại Gateway
public class ProjectController {

  private final ProjectAppService service;

  @PostMapping
  public Project create(@RequestBody CreateProjectReq req) {
    return service.create(req.title(), req.description(), req.syllabusId());
  }

  @GetMapping
  public List<Project> getAll() {
    return service.getAll();
  }

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
    return service.assignToClass(id, classId);
  }
}
