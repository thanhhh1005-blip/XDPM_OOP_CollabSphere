package com.collab.projectservice.domain;

public enum ProjectStatus {
    DRAFT,      // Bản nháp
    PENDING,    // Chờ duyệt
    APPROVED,   // Đã duyệt
    ASSIGNED,   // Đã giao lớp
    DENIED,     // Bị từ chối (Bổ sung giá trị này để sửa lỗi)
    REJECTED    // Bạn có thể giữ cả hai hoặc gộp lại
}