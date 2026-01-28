package com.collab.workspaceservice.repository;

import com.collab.workspaceservice.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    
    Optional<Workspace> findByTeamId(String teamId);
    boolean existsByTeamId(String teamId);

    Optional<Workspace> findByClassIdAndTeamIdIsNull(Long classId);
    
    boolean existsByClassIdAndTeamIdIsNull(Long classId);
}