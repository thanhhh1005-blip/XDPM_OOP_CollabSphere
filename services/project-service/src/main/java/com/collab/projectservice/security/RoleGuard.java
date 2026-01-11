public class RoleGuard {
  public static void require(String role, String... allow) {
    if (role == null) {
      throw new ForbiddenException("Missing role");
    }
    for (String r : allow) {
      if (r.equalsIgnoreCase(role)) return;
    }
    throw new ForbiddenException("Permission denied for role: " + role);
  }
}
