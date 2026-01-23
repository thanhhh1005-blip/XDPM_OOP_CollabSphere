package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Hiện tại để trống, JpaRepository đã có sẵn hàm save(), findAll() rồi
    List<Task> findBySprintId(Long sprintId);
    List<Task> findByWorkspaceId(Long workspaceId);
    List<Task> findBySprintWorkspaceId(Long workspaceId);
    List<Task> findByWorkspaceIdAndTeamId(Long workspaceId, String teamId);
    List<Task> findByWorkspaceIdAndClassId(Long workspaceId, Long classId);
}