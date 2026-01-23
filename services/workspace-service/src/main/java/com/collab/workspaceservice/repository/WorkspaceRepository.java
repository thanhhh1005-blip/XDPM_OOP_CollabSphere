package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    // Tìm workspace theo mã nhóm
    Optional<Workspace> findByTeamId(String teamId);
    
    // Kiểm tra xem nhóm đã có workspace chưa
    boolean existsByTeamId(String teamId);

    Optional<Workspace> findByClassId(Long classId);
    boolean existsByClassId(Long classId);
}