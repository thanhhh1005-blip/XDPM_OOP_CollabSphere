import axios from 'axios';

/**
 * ===================== CONFIG =====================
 * Đi qua API Gateway
 * Ví dụ Gateway chạy port 8080
 */
const API_BASE = '/api/resources';
// hoặc nếu chưa proxy:
// const API_BASE = 'http://localhost:8080/api/resources';

/**
 * Axios instance riêng cho Resource
 * → dễ gắn interceptor (JWT, refresh token…)
 */
const resourceApi = axios.create({
  baseURL: API_BASE,
  timeout: 15000,
});

/* ===================== API FUNCTIONS ===================== */

/**
 * Lấy danh sách resource
 * @param {Object} params { keyword, type }
 */
export const getResources = async (params = {}) => {
  try {
    const res = await resourceApi.get('/', { params });
    return res.data; // FE đang dùng trực tiếp array
  } catch (error) {
    console.error('getResources error:', error);
    throw error;
  }
};

/**
 * Upload resource
 * @param {FormData} formData
 */
export const uploadResource = async (formData) => {
  try {
    const res = await resourceApi.post('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return res.data;
  } catch (error) {
    console.error('uploadResource error:', error);
    throw error;
  }
};

/**
 * Xóa resource theo id
 * @param {number|string} id
 */
export const deleteResource = async (id) => {
  try {
    const res = await resourceApi.delete(`/${id}`);
    return res.data;
  } catch (error) {
    console.error('deleteResource error:', error);
    throw error;
  }
};

/**
 * Download / Preview resource
 * @param {number|string} id
 * @returns Blob
 */
export const downloadResource = async (id) => {
  try {
    const res = await resourceApi.get(`/download/${id}`, {
      responseType: 'blob',
    });
    return res.data;
  } catch (error) {
    console.error('downloadResource error:', error);
    throw error;
  }
};
