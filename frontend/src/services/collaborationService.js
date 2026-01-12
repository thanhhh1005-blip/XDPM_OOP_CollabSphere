import axios from 'axios';

/**
 * ===================== CONFIG =====================
 * Qua API Gateway
 */
const API_BASE = 'http://localhost:8090/api/collaborations';

const collaborationApi = axios.create({
  baseURL: API_BASE,
});

/* ===================== API ===================== */

export const getCollaborations = async () => {
  const res = await collaborationApi.get('/');
  return res.data.data; // ApiResponse
};

export const createCollaboration = async (payload) => {
  const res = await collaborationApi.post('/', payload);
  return res.data.data;
};

export const deleteCollaboration = async (id) => {
  await collaborationApi.delete(`/${id}`);
};

export const addMember = async (id, userId) => {
  await collaborationApi.post(`/${id}/members`, { userId });
};

export const removeMember = async (id, userId) => {
  await collaborationApi.delete(`/${id}/members/${userId}`);
};
