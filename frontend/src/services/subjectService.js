import axios from 'axios';

const API_URL = "http://localhost:8080/api/v1/subjects";

// 1. Lấy danh sách
export const getAllSubjects = () => axios.get(API_URL).then(res => res.data);

// 2. Tạo mới
export const createSubject = (data) => axios.post(API_URL, data);

// 3. Cập nhật (MỚI)
export const updateSubject = (id, data) => axios.put(`${API_URL}/${id}`, data);

// 4. Xóa (MỚI)
export const deleteSubject = (id) => axios.delete(`${API_URL}/${id}`);

// 5. Import Excel (MỚI)
export const importSubjects = (file) => {
    let formData = new FormData();
    formData.append("file", file);
    return axios.post(`${API_URL}/import`, formData, {
        headers: { "Content-Type": "multipart/form-data" }
    });
};

// 6. Lấy theo Code (nếu cần)
export const getSubjectByCode = (code) => axios.get(`${API_URL}/code/${code}`);