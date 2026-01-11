package com.collab.projectservice.security;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityUtil {

    public static Role getRole(HttpServletRequest request) {
        String raw = request.getHeader("X-ROLE");
        if (raw == null || raw.isBlank()) return null;
        try {
            return Role.valueOf(raw.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUserId(HttpServletRequest request) {
        String raw = request.getHeader("X-USER-ID");
        return (raw == null || raw.isBlank()) ? null : raw.trim();
    }

    public static void requireRole(HttpServletRequest request, Role... allowed) {
        Role role = getRole(request);
        if (role == null) throw new ForbiddenException("Missing or invalid role (X-ROLE)");

        for (Role r : allowed) {
            if (role == r) return;
        }
        throw new ForbiddenException("Forbidden: role=" + role);
    }
}
