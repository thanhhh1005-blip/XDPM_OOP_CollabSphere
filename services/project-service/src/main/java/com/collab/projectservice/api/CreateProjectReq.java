package com.collab.projectservice.api;

/**
 * Record tách biệt để tránh lỗi ClassNotFoundException khi introspect Controller.
 */
public record CreateProjectReq(String title, String description, String syllabusId) {}