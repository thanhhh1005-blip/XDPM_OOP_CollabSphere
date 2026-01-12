import { jwtDecode } from "jwt-decode";

export function getToken() {
  return localStorage.getItem("token") || localStorage.getItem("access_token");
}

export function getAuthInfo() {
  const token = getToken();
  if (!token) return { token: null, role: null, userId: null };

  try {
    const payload = jwtDecode(token);

    // ⚠️ Tùy backend đặt claim tên gì, mình để fallback nhiều kiểu:
    const role =
      payload?.role ||
      payload?.roles?.[0] ||
      payload?.authorities?.[0] ||
      payload?.scope ||
      null;

    const userId =
      payload?.userId ||
      payload?.id ||
      payload?.sub || // nhiều hệ thống để username trong sub
      null;

    return { token, role, userId, payload };
  } catch (e) {
    // token không phải JWT hoặc decode fail
    return { token, role: null, userId: null };
  }
}
