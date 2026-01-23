package com.collab.collaborationservice.repository;

import com.collab.collaborationservice.entity.EditorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorRepository extends JpaRepository<EditorData, String> {
}