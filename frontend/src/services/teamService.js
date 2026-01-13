// E:\XDPMHDT(2)\XDPM_OOP_CollabSphere\frontend\src\services\teamService.js
import axios from "axios";
import { getAuthInfo } from "../utils/authStorage";

const API_BASE = "http://localhost:8080/api/v1/teams";

function buildHeaders() {
  const auth = getAuthInfo() || {};
  const token =
    auth.token ||
    auth.accessToken ||
    localStorage.getItem("token") ||
    localStorage.getItem("accessToken");

  return {
    "X-ROLE": auth.role,
    "X-USER-ID": auth.userId,
    Authorization: token ? `Bearer ${token}` : undefined,
  };
}

export async function createTeam({ name, classId, projectId }) {
  const headers = buildHeaders();

  // Backend đang dùng @RequestParam => gửi bằng query params
  const params = { name, classId };
  if (projectId) params.projectId = projectId;

  const res = await axios.post(API_BASE, null, { headers, params });
  return res.data;
}
