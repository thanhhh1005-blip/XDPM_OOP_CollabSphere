import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { getAuthInfo } from '../../utils/authStorage';

const ProjectList = () => {
  const [projects, setProjects] = useState([]);
  const navigate = useNavigate();

  // --- LẤY THÔNG TIN USER ---
  const auth = getAuthInfo() || {};
  // Lưu ý: Nếu bạn đang test, hãy chắc chắn role trong localStorage đúng là 'LECTURER' hoặc 'HEAD_DEPARTMENT'
  const { role } = auth; 

  const isLecturer = role === 'LECTURER';
  const isHead = role === 'HEAD_DEPARTMENT'; // Role Trưởng bộ môn
  const isStudent = role === 'STUDENT';

  const API_BASE_URL = 'http://localhost:8080/api/v1/projects';

  const [openDesc, setOpenDesc] = useState(false);
  const [descProject, setDescProject] = useState(null);

  // --- HEADERS AUTH ---
  const token = auth.token || localStorage.getItem('token');
  
  const authHeaders = useMemo(() => {
    const headers = {};
    if (token) headers.Authorization = `Bearer ${token}`;
    if (auth?.role) headers['X-ROLE'] = auth.role;
    if (auth?.userId) headers['X-USER-ID'] = String(auth.userId);
    return headers;
  }, [token, auth?.role, auth?.userId]);

  // --- FETCH DATA ---
  const fetchProjects = () => {
    axios
      .get(API_BASE_URL, { headers: authHeaders })
      .then((response) => {
        if (response.data && Array.isArray(response.data.result)) {
            setProjects(response.data.result);
        } else if (Array.isArray(response.data)) {
            setProjects(response.data);
        } else {
            setProjects([]);
        }
      })
      .catch((error) => console.error('Lỗi lấy dữ liệu:', error));
  };

  useEffect(() => {
    fetchProjects();
  }, []);

  // --- XỬ LÝ HÀNH ĐỘNG (Nộp, Duyệt, Từ chối) ---
  const handleAction = (id, action) => {
    // Xác nhận trước khi thao tác
    if (!window.confirm(`Bạn có chắc muốn thực hiện "${action}" dự án này?`)) return;

    axios
      .post(`${API_BASE_URL}/${id}/${action}`, {}, { headers: authHeaders })
      .then(() => {
        alert("Thành công!");

        // 🔥 OPTIMISTIC UPDATE: Cập nhật giao diện ngay lập tức mà không cần F5
        setProjects((prev) => 
          prev.map((p) => {
            if (p.id === id) {
              let newStatus = p.status;
              if (action === 'submit') newStatus = 'PENDING';
              if (action === 'approve') newStatus = 'APPROVED';
              if (action === 'deny') newStatus = 'DENIED';
              return { ...p, status: newStatus };
            }
            return p;
          })
        );
      })
      .catch((error) => {
        alert('Lỗi: ' + (error.response?.data?.message || error.message));
      });
  };

  // --- HELPER HIỂN THỊ BADGE ---
  const getStatusBadge = (status) => {
    const s = status || 'DRAFT'; // Mặc định là DRAFT nếu null
    let color = '#374151'; 
    let bg = '#f3f4f6';

    if (s === 'APPROVED') { color = '#166534'; bg = '#dcfce7'; }
    if (s === 'PENDING') { color = '#854d0e'; bg = '#fef3c7'; }
    if (s === 'DENIED') { color = '#991b1b'; bg = '#fee2e2'; }

    return (
      <span style={{ padding: '4px 10px', borderRadius: 99, fontSize: 12, fontWeight: 800, color, background: bg }}>
        {s}
      </span>
    );
  };

  return (
    <div style={{ padding: 20 }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 20 }}>
        <h2>Quản lý Dự án ({role})</h2>
        {isLecturer && (
          <button 
            onClick={() => navigate('/projects/new')}
            style={{ padding: '8px 16px', background: '#2563eb', color: '#fff', border: 'none', borderRadius: 6, cursor: 'pointer', fontWeight: 'bold' }}
          >
            + Tạo Dự án Mẫu
          </button>
        )}
      </div>

      {/* Table */}
      <table style={{ width: '100%', borderCollapse: 'collapse', background: '#fff', border: '1px solid #ddd' }}>
        <thead>
          <tr style={{ background: '#f9fafb', textAlign: 'left' }}>
            <th style={{ padding: 12 }}>Tiêu đề</th>
            <th style={{ padding: 12 }}>Mô tả</th>
            <th style={{ padding: 12, textAlign: 'center' }}>Trạng thái</th>
            <th style={{ padding: 12, textAlign: 'center' }}>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {projects.map((p) => {
            const currentStatus = p.status || 'DRAFT';

            return (
              <tr key={p.id} style={{ borderTop: '1px solid #eee' }}>
                <td style={{ padding: 12, fontWeight: 600 }}>{p.title}</td>
                <td style={{ padding: 12, color: '#666', fontSize: 14 }}>{p.description}</td>
                <td style={{ padding: 12, textAlign: 'center' }}>
                  {getStatusBadge(currentStatus)}
                </td>
                <td style={{ padding: 12, textAlign: 'center' }}>
                  
                  {/* 👉 LOGIC HIỆN NÚT CHO GIẢNG VIÊN */}
                  {isLecturer && currentStatus === 'DRAFT' && (
                    <button 
                      onClick={() => handleAction(p.id, 'submit')}
                      style={{ padding: '6px 12px', background: '#f59e0b', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}
                    >
                      Gửi duyệt
                    </button>
                  )}

                  {/* 👉 LOGIC HIỆN NÚT CHO TRƯỞNG BỘ MÔN (HEAD) */}
                  {isHead && currentStatus === 'PENDING' && (
                    <div style={{ display: 'flex', gap: 8, justifyContent: 'center' }}>
                      <button 
                        onClick={() => handleAction(p.id, 'approve')}
                        style={{ padding: '6px 12px', background: '#16a34a', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}
                      >
                        Duyệt
                      </button>
                      <button 
                        onClick={() => handleAction(p.id, 'deny')}
                        style={{ padding: '6px 12px', background: '#dc2626', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}
                      >
                        Từ chối
                      </button>
                    </div>
                  )}

                  {/* Đã duyệt rồi thì hiện text báo */}
                  {currentStatus === 'APPROVED' && <span style={{fontSize: 12, color: 'green'}}>✅ Đã khả dụng</span>}
                  {currentStatus === 'DENIED' && <span style={{fontSize: 12, color: 'red'}}>⛔ Đã đóng</span>}
                  
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default ProjectList;