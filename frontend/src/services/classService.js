import axios from 'axios';

// Đảm bảo đúng cổng của Backend (Spring Boot)
const API_BASE_URL = "http://localhost:8080/api/v1/classes";

// --- CÁC HÀM GỌI API ---

export const getAllClasses = () => {
    return axios.get(API_BASE_URL).then(res => res.data);
};

export const createClass = (classData) => {
    return axios.post(API_BASE_URL, classData);
};

export const getClassById = (id) => {
    return axios.get(`${API_BASE_URL}/${id}`);
};

export const updateClass = (id, classData) => {
    return axios.put(`${API_BASE_URL}/${id}`, classData);
};

export const deleteClass = (id) => {
    return axios.delete(`${API_BASE_URL}/${id}`);
};

export const importClasses = (file) => {
    let formData = new FormData();
    formData.append("file", file);
    return axios.post(`${API_BASE_URL}/import`, formData, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    });
};

export const addStudentToClass = (classId, studentId) => {
    // Lưu ý: Backend của bạn dùng @RequestParam cho studentId
    // Nên URL sẽ là: .../students?studentId=...
    return axios.post(`${API_BASE_URL}/${classId}/students`, null, {
        params: { studentId: studentId }
    });
};

export const getStudentsInClass = (classId) => {
    return axios.get(`${API_BASE_URL}/${classId}/students`).then(res => res.data);
};