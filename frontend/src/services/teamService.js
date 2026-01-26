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

// Bạn nên nhận thêm leaderId và memberIds để gửi đủ thông tin
export async function createTeam(data) {
  // data là object chứa: { name, classId, projectId, leaderId, memberIds }
  const headers = buildHeaders();

  // 👇 SỬA LẠI: Gửi data vào vị trí thứ 2 (Body), không dùng 'params' nữa
  const res = await axios.post(API_BASE, data, { headers });
  
  return res.data;
}
