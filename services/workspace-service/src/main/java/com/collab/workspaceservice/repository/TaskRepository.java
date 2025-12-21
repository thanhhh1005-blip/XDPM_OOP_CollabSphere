package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Hiện tại để trống, JpaRepository đã có sẵn hàm save(), findAll() rồi
}