import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useHistory } from 'react-router-dom';

const ProjectList = () => {
    const [projects, setProjects] = useState([]);
    const history = useHistory();
    // Thay đổi port sang 8080 để đi qua API Gateway
    const API_BASE_URL = 'http://localhost:8080/api/v1/projects';

    const fetchProjects = () => {
        axios.get(API_BASE_URL)
            .then(response => setProjects(response.data))
            .catch(error => console.error('Lỗi lấy dữ liệu:', error));
    };

    useEffect(() => {
        fetchProjects();
    }, []);

    const handleAction = (id, action) => {
        axios.post(`${API_BASE_URL}/${id}/${action}`)
            .then(() => {
                alert(`Thực hiện ${action} thành công!`);
                fetchProjects();
            })
            .catch(error => alert('Lỗi: ' + (error.response?.data?.message || error.message)));
    };

    // Bổ sung chức năng Giao cho lớp
    const handleAssign = (id) => {
        const classId = prompt("Vui lòng nhập mã lớp học để giao dự án:");
        if (classId) {
            axios.post(`${API_BASE_URL}/${id}/assign/${classId}`)
                .then(() => {
                    alert("Giao dự án cho lớp thành công!");
                    fetchProjects();
                })
                .catch(error => alert('Lỗi giao lớp: ' + error.message));
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                <h1>Danh sách Dự án CollabSphere</h1>
                <button onClick={() => history.push('/create-project')} 
                        style={{ padding: '10px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    + Tạo Dự án Mẫu
                </button>
            </div>

            <table style={{ width: '100%', borderCollapse: 'collapse' }} border="1">
                <thead>
                    <tr style={{ backgroundColor: '#f2f2f2' }}>
                        <th>Tiêu đề</th>
                        <th>Mô tả</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    {projects.map(project => (
                        <tr key={project.id}>
                            <td style={{ padding: '10px' }}>{project.title}</td>
                            <td style={{ padding: '10px' }}>{project.description}</td>
                            <td style={{ padding: '10px', textAlign: 'center' }}>
                                <span style={{ 
                                    padding: '4px 8px', borderRadius: '4px', 
                                    backgroundColor: project.status === 'APPROVED' ? '#d4edda' : project.status === 'PENDING' ? '#fff3cd' : '#f8f9fa' 
                                }}>
                                    {project.status}
                                </span>
                            </td>
                            <td style={{ padding: '10px' }}>
                                {project.status === 'DRAFT' && (
                                    <button onClick={() => handleAction(project.id, 'submit')}>Nộp duyệt</button>
                                )}

                                {project.status === 'PENDING' && (
                                    <>
                                        <button onClick={() => handleAction(project.id, 'approve')} style={{ color: 'green' }}>Duyệt</button>
                                        <button onClick={() => handleAction(project.id, 'deny')} style={{ color: 'red' }}>Từ chối</button>
                                    </>
                                )}

                                {project.status === 'APPROVED' && (
                                    <button onClick={() => handleAssign(project.id)} style={{ backgroundColor: '#007bff', color: 'white' }}>
                                        Giao cho lớp
                                    </button>
                                )}
                                
                                <button onClick={() => history.push(`/projects/${project.id}`)} style={{ marginLeft: '5px' }}>Chi tiết</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ProjectList;