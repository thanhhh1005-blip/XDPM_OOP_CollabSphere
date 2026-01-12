package com.collab.projectservice.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RoleGuard {

  private RoleGuard() {}

  public static void require(String role, String requiredRole) {
    if (role == null || role.isBlank()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing X-ROLE header");
    }
    if (!requiredRole.equalsIgnoreCase(role)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Forbidden: required role " + requiredRole + ", but got " + role);
    }
  }
}
