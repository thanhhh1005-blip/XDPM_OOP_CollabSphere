package com.collab.resourceservice.repository;

import com.collab.resourceservice.entity.Resource;
import com.collab.resourceservice.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByDeletedFalse();

    Optional<Resource> findByStoredFileName(String storedFileName);

    List<Resource> findAllByDeletedFalse();

    List<Resource> findByUploadedByAndDeletedFalse(String uploadedBy);

    List<Resource> findByTypeAndDeletedFalse(ResourceType type);

    boolean existsByIdAndDeletedFalse(Long id);
}
