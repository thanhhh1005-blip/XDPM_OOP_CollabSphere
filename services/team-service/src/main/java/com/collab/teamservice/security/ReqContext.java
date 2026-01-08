package com.collab.teamservice.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class ReqContext {
  private final String role;
  private final String userId;

  public ReqContext(String role, String userId) {
    this.role = role == null ? "ANON" : role;
    this.userId = userId == null ? "anonymous" : userId;
  }

  public static ReqContext from(HttpServletRequest req) {
    return new ReqContext(req.getHeader("X-ROLE"), req.getHeader("X-USER-ID"));
  }

  public boolean isLecturer() { return "LECTURER".equalsIgnoreCase(role); }
  public boolean isHead() { return "HEAD_DEPARTMENT".equalsIgnoreCase(role); }
  public boolean isStudent() { return "STUDENT".equalsIgnoreCase(role); }
}
