import axios from "axios";
import { getAuthInfo } from "../utils/authStorage";

const http = axios.create({
  baseURL: "http://localhost:8080",
  headers: { "Content-Type": "application/json" },
});

http.interceptors.request.use((config) => {
  const { token, role, userId } = getAuthInfo();

  config.headers = config.headers || {};

  // gửi token nếu BE kiểm tra
  if (token) config.headers.Authorization = `Bearer ${token}`;

  // gửi thêm header role/userId để project-service chặn quyền (nếu bạn dùng cách này)
  if (role) config.headers["X-ROLE"] = role;
  if (userId) config.headers["X-USER-ID"] = String(userId);

  return config;
});

const BASE = "/api/v1/projects";

export const projectService = {
  getAll: async () => (await http.get(BASE)).data,
  getById: async (id) => (await http.get(`${BASE}/${id}`)).data,
  create: async ({ title, description, syllabusId }) =>
    (await http.post(BASE, { title, description, syllabusId })).data,
  submit: async (id) => (await http.post(`${BASE}/${id}/submit`)).data,
  approve: async (id) => (await http.post(`${BASE}/${id}/approve`)).data,
  deny: async (id) => (await http.post(`${BASE}/${id}/deny`)).data,
  assignToClass: async (id, classId) =>
    (await http.post(`${BASE}/${id}/assign/${classId}`)).data,
};
