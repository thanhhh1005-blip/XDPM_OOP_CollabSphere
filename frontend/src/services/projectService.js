import axios from "axios";

const http = axios.create({
  baseURL: "http://localhost:8080", // đi qua gateway
  headers: { "Content-Type": "application/json" },
});

const BASE = "/api/v1/projects";

export const getProjects = async () => (await http.get(BASE)).data;

export const createProject = async (payload) => (await http.post(BASE, payload)).data;

export const submitProject = async (id) => (await http.post(`${BASE}/${id}/submit`)).data;

export const approveProject = async (id) => (await http.post(`${BASE}/${id}/approve`)).data;

export const denyProject = async (id) => (await http.post(`${BASE}/${id}/deny`)).data;

export const assignProject = async (id, classId) =>
  (await http.post(`${BASE}/${id}/assign/${classId}`)).data;
