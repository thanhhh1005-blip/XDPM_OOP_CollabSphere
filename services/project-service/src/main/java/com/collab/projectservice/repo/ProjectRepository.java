package com.collab.projectservice.repo;

import com.collab.projectservice.domain.Project;
import com.collab.projectservice.domain.ProjectStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Sửa kiểu ID từ String thành Long
public interface ProjectRepository extends JpaRepository<Project, Long> {
  List<Project> findByStatus(ProjectStatus status);
}