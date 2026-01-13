package com.collab.teamservice.security;

public class RoleGuard {

  public static void require(String role, String expected) {
    if (role == null || role.isBlank()) {
      throw new RuntimeException("Thiếu X-ROLE");
    }
    if (!role.equals(expected)) {
      throw new RuntimeException("Không có quyền: cần " + expected);
    }
  }
}
