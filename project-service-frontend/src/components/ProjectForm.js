import React, { useState } from 'react';
import axios from 'axios';
import { useHistory } from 'react-router-dom';

const ProjectForm = ({ project }) => {
    const [title, setTitle] = useState(project ? project.title : '');
    const [description, setDescription] = useState(project ? project.description : '');
    // Bổ sung syllabusId để khớp với CreateProjectReq của Backend
    const [syllabusId, setSyllabusId] = useState(project ? project.syllabusId : '');
    const history = useHistory();

    const API_URL = 'http://localhost:8080/api/v1/projects';

    const handleSubmit = (e) => {
        e.preventDefault();
        const payload = { title, description, syllabusId };

        const request = project 
            ? axios.put(`${API_URL}/${project.id}`, payload)
            : axios.post(API_URL, payload);

        request.then(() => {
            alert(project ? "Cập nhật thành công!" : "Tạo dự án mới thành công!");
            history.push('/projects'); // Tự động quay về danh sách sau khi lưu
        }).catch(err => alert("Lỗi: " + err.message));
    };

    return (
        <div style={{ padding: '20px', maxWidth: '600px' }}>
            <h1>{project ? 'Chỉnh sửa dự án' : 'Tạo dự án mẫu mới'}</h1>
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div>
                    <label style={{ display: 'block' }}>Tiêu đề dự án:</label>
                    <input style={{ width: '100%', padding: '8px' }} type="text" value={title} onChange={(e) => setTitle(e.target.value)} required />
                </div>
                <div>
                    <label style={{ display: 'block' }}>Mã đề cương (Syllabus ID):</label>
                    <input style={{ width: '100%', padding: '8px' }} type="text" value={syllabusId} onChange={(e) => setSyllabusId(e.target.value)} placeholder="Ví dụ: SYL-SE101" required />
                </div>
                <div>
                    <label style={{ display: 'block' }}>Mô tả chi tiết:</label>
                    <textarea style={{ width: '100%', padding: '8px', height: '100px' }} value={description} onChange={(e) => setDescription(e.target.value)} />
                </div>
                <div style={{ display: 'flex', gap: '10px' }}>
                    <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#007bff', color: 'white', border: 'none', cursor: 'pointer' }}>
                        Lưu dự án
                    </button>
                    <button type="button" onClick={() => history.push('/projects')} style={{ padding: '10px 20px' }}>
                        Hủy bỏ
                    </button>
                </div>
            </form>
        </div>
    );
};

export default ProjectForm;