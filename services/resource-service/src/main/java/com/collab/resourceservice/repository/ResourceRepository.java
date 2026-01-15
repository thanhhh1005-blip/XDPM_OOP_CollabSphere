package com.collab.resourceservice.repository;

import com.collab.resourceservice.entity.Resource;
import com.collab.resourceservice.enums.ResourceScope;
import com.collab.resourceservice.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    // 1. Tìm tất cả file đang hoạt động (chưa bị xóa mềm)
    List<Resource> findByDeletedFalse();

    // 2. Tìm file theo tên lưu trữ (quan trọng để lấy file từ MinIO/S3)
    Optional<Resource> findByStoredFileNameAndDeletedFalse(String storedFileName);

    // 3. Lấy danh sách tài liệu theo Lớp hoặc Nhóm
    // Ví dụ: Tìm tài liệu của lớp "SE104" -> findByScopeAndScopeId(ResourceScope.CLASS, "SE104")
    List<Resource> findByScopeAndScopeIdAndDeletedFalse(ResourceScope scope, String scopeId);

    // 4. Tìm tài liệu do một người cụ thể upload
    List<Resource> findByUploadedByAndDeletedFalse(String uploadedBy);

    // 5. Lọc tài liệu theo loại (VD: Lấy toàn bộ file PDF)
    List<Resource> findByTypeAndDeletedFalse(ResourceType type);

    // 6. Tìm chi tiết theo ID (Đảm bảo file đó chưa bị xóa)
    Optional<Resource> findByIdAndDeletedFalse(Long id);

    List<Resource> findByScopeAndScopeIdOrderByCreatedAtDesc(ResourceScope scope, String scopeId);
}