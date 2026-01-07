package com.collab.projectservice.repo;

import com.collab.projectservice.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {}
