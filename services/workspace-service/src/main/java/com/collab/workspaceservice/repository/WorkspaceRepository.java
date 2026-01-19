package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    // Tìm workspace theo mã nhóm
    Optional<Workspace> findByTeamId(Long teamId);
    
    // Kiểm tra xem nhóm đã có workspace chưa
    boolean existsByTeamId(Long teamId);
}