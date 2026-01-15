package com.collab.resourceservice.enums;

public enum UserRole {
    STAFF,
    LECTURER,
    LEADER,       // Dùng cho tương lai (khi phân quyền trong nhóm)
    HEAD_DEPARTMENT,
    MEMBER,       // Dùng cho tương lai
    STUDENT,
    ADMIN,
    USER          // <--- QUAN TRỌNG: Phải thêm cái này để khớp với Database hiện tại
}