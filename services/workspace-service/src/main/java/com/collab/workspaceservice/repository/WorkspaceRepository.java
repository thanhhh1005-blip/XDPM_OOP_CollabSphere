package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    
    // 1. Dành cho Team: Tìm theo ID nhóm
    Optional<Workspace> findByTeamId(String teamId);
    boolean existsByTeamId(String teamId);

    // 2. Dành cho Lớp (QUAN TRỌNG): 
    // Tìm Workspace chung của lớp (Phải đảm bảo teamId là NULL)
    // Nếu dùng findByClassId khơi khơi, nó có thể lấy nhầm workspace của 1 team trong lớp đó.
    Optional<Workspace> findByClassIdAndTeamIdIsNull(Long classId);
    
    boolean existsByClassIdAndTeamIdIsNull(Long classId);
}