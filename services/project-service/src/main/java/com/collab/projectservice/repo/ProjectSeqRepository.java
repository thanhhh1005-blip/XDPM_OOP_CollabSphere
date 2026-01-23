package com.collab.projectservice.repo;

import com.collab.projectservice.domain.ProjectSeq;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface ProjectSeqRepository extends JpaRepository<ProjectSeq, Integer> {

  // ✅ lock dòng để tránh trùng code khi nhiều request đồng thời
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select s from ProjectSeq s where s.id = :id")
  ProjectSeq lockById(@Param("id") Integer id);
}
