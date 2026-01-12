import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { getAuthInfo } from '../../utils/authStorage'; // ✅ ADD

const ProjectList = () => {
  const [projects, setProjects] = useState([]);
  const navigate = useNavigate();

  // ✅ lấy auth info (KHÔNG sửa Login.jsx)
  const auth = getAuthInfo() || {};
  const { role } = auth;

  const isLecturer = role === 'LECTURER';
  const isHead = role === 'HEAD_DEPARTMENT';
  const isStudent = role === 'STUDENT';

  // ✅ đi qua API Gateway
  const API_BASE_URL = 'http://localhost:8080/api/v1/projects';

  // modal state
  const [openDesc, setOpenDesc] = useState(false);
  const [descProject, setDescProject] = useState(null);

  // ✅ Lấy token + userId (fallback localStorage)
  const token =
    auth.token ||
    auth.accessToken ||
    localStorage.getItem('token') ||
    localStorage.getItem('accessToken') ||
    JSON.parse(localStorage.getItem('user') || '{}')?.token ||
    JSON.parse(localStorage.getItem('user') || '{}')?.accessToken;

  const authHeaders = useMemo(() => {
    const headers = {};

    // Bearer token (nếu backend phía trước cần)
    if (token) headers.Authorization = `Bearer ${token}`;

    // ✅ project-service của bạn đang đọc 2 header này
    if (auth?.role) headers['X-ROLE'] = auth.role;
    if (auth?.userId) headers['X-USER-ID'] = String(auth.userId);

    // fallback nếu authStorage không có userId/role
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (!headers['X-ROLE'] && user?.role) headers['X-ROLE'] = user.role;

    const fallbackUserId = user?.userId ?? user?.id;
    if (!headers['X-USER-ID'] && fallbackUserId != null) {
      headers['X-USER-ID'] = String(fallbackUserId);
    }

    return headers;
  }, [token, auth?.role, auth?.userId]);

  const fetchProjects = () => {
    axios
      .get(API_BASE_URL, { headers: authHeaders })
      .then((response) => setProjects(response.data))
      .catch((error) => console.error('Lỗi lấy dữ liệu:', error));
  };

  useEffect(() => {
    fetchProjects();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ✅ KHÓA SCROLL nền khi mở modal (fix lỗi giống “chuyển trang”)
  useEffect(() => {
    if (openDesc) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
    return () => {
      document.body.style.overflow = 'auto';
    };
  }, [openDesc]);

  const handleAction = (id, action) => {
    axios
      .post(`${API_BASE_URL}/${id}/${action}`, {}, { headers: authHeaders })
      .then(() => {
        alert(`Thực hiện ${action} thành công!`);
        fetchProjects();
      })
      .catch((error) =>
        alert('Lỗi: ' + (error.response?.data?.message || error.message))
      );
  };

  const handleAssign = (id) => {
    const classId = prompt('Vui lòng nhập mã lớp học để giao dự án:');
    if (classId) {
      axios
        .post(`${API_BASE_URL}/${id}/assign/${classId}`, {}, { headers: authHeaders })
        .then(() => {
          alert('Giao dự án cho lớp thành công!');
          fetchProjects();
        })
        .catch((error) =>
          alert('Lỗi giao lớp: ' + (error.response?.data?.message || error.message))
        );
    }
  };

  const openDescriptionModal = (project) => {
    setDescProject(project);
    setOpenDesc(true);
  };

  const closeDescriptionModal = () => {
    setOpenDesc(false);
    setDescProject(null);
  };

  const statusBadgeClass = useMemo(
    () => (status) => {
      if (status === 'APPROVED') return 'badge badge-approved';
      if (status === 'PENDING') return 'badge badge-pending';
      if (status === 'DENIED') return 'badge badge-denied';
      return 'badge badge-draft';
    },
    []
  );

  const btnStyle = {
    width: '100%',
    padding: '6px 10px',
    borderRadius: 6,
    border: '1px solid #d1d5db',
    background: '#f9fafb',
    cursor: 'pointer',
    fontWeight: 600,
  };

  return (
    <div style={{ padding: '20px' }}>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: 16,
          gap: 12,
        }}
      >
        <h1 style={{ margin: 0 }}>Danh sách Dự án CollabSphere</h1>

        {/* ✅ CHỈ LECTURER ĐƯỢC TẠO DỰ ÁN */}
        {isLecturer && (
          <button
            onClick={() => navigate('/projects/new')}
            style={{
              padding: '10px 14px',
              backgroundColor: '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: 8,
              cursor: 'pointer',
              fontWeight: 600,
            }}
          >
            + Tạo Dự án Mẫu
          </button>
        )}
      </div>

      <div
        style={{
          overflowX: 'auto',
          width: '100%',
          border: '1px solid #e5e7eb',
          borderRadius: 10,
        }}
      >
        <table
          style={{
            width: '100%',
            borderCollapse: 'collapse',
            tableLayout: 'fixed',
            background: '#fff',
          }}
        >
          <colgroup>
            <col style={{ width: '18%' }} />
            <col style={{ width: '34%' }} />
            <col style={{ width: '16%' }} />
            <col style={{ width: '10%' }} />
          </colgroup>

          <thead>
            <tr style={{ backgroundColor: '#f2f2f2' }}>
              <th style={{ padding: 10, textAlign: 'left' }}>Tiêu đề</th>
              <th style={{ padding: 10, textAlign: 'left' }}>Mô tả</th>
              <th style={{ padding: 10, textAlign: 'center' }}>Trạng thái</th>
              <th style={{ padding: 10, textAlign: 'center' }}>Thao tác</th>
            </tr>
          </thead>

          <tbody>
            {projects.map((project) => (
              <tr key={project.id} style={{ borderTop: '1px solid #eee' }}>
                <td style={{ padding: 10, fontWeight: 600 }}>{project.title}</td>

                <td style={{ padding: 10 }}>
                  <div
                    onClick={() => openDescriptionModal(project)}
                    style={{
                      cursor: 'pointer',
                      display: '-webkit-box',
                      WebkitLineClamp: 1,
                      WebkitBoxOrient: 'vertical',
                      overflow: 'hidden',
                      wordBreak: 'break-word',
                    }}
                    title="Bấm để xem đầy đủ mô tả"
                  >
                    {project.description || '(Không có mô tả)'}
                  </div>

                  <button
                    type="button"
                    onClick={() => openDescriptionModal(project)}
                    style={{
                      marginTop: 4,
                      padding: 0,
                      border: 'none',
                      background: 'transparent',
                      color: '#2563eb',
                      cursor: 'pointer',
                      fontWeight: 600,
                      fontSize: 12,
                    }}
                  >
                    Xem đầy đủ
                  </button>
                </td>

                <td style={{ padding: 10, textAlign: 'center' }}>
                  <span className={statusBadgeClass(project.status)}>{project.status}</span>
                </td>

                <td style={{ padding: 10 }}>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                    {/* ✅ SUBMIT: chỉ LECTURER */}
                    {isLecturer && project.status === 'DRAFT' && (
                      <button
                        type="button"
                        style={btnStyle}
                        onClick={() => handleAction(project.id, 'submit')}
                      >
                        Nộp duyệt
                      </button>
                    )}

                    {/* ✅ APPROVE / DENY: chỉ HEAD_DEPARTMENT */}
                    {isHead && project.status === 'PENDING' && (
                      <>
                        <button
                          type="button"
                          style={{ ...btnStyle, color: 'green' }}
                          onClick={() => handleAction(project.id, 'approve')}
                        >
                          Duyệt
                        </button>
                        <button
                          type="button"
                          style={{ ...btnStyle, color: 'red' }}
                          onClick={() => handleAction(project.id, 'deny')}
                        >
                          Từ chối
                        </button>
                      </>
                    )}

                    {/* ✅ ASSIGN: chỉ HEAD_DEPARTMENT */}
                    {isHead && project.status === 'APPROVED' && (
                      <button
                        type="button"
                        style={{
                          ...btnStyle,
                          background: '#007bff',
                          color: 'white',
                          borderColor: '#007bff',
                        }}
                        onClick={() => handleAssign(project.id)}
                      >
                        Giao cho lớp
                      </button>
                    )}

                    {/* Student không có nút thao tác */}
                    {isStudent && <span style={{ fontSize: 12, color: '#6b7280' }}>—</span>}
                  </div>
                </td>
              </tr>
            ))}

            {projects.length === 0 && (
              <tr>
                <td colSpan="4" style={{ textAlign: 'center', padding: 14 }}>
                  Không có dự án
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* ✅ MODAL: fix full-screen + cuộn nội dung bên trong */}
      {openDesc && (
        <div
          onClick={closeDescriptionModal}
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(0,0,0,0.45)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            padding: 16,
            zIndex: 9999,
          }}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            style={{
              width: 'min(900px, 100%)',
              maxHeight: '90vh',
              background: '#fff',
              borderRadius: 12,
              boxShadow: '0 10px 30px rgba(0,0,0,0.25)',
              overflow: 'hidden',
              display: 'flex',
              flexDirection: 'column',
            }}
          >
            <div
              style={{
                padding: 14,
                borderBottom: '1px solid #eee',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                gap: 12,
              }}
            >
              <div>
                <div style={{ fontWeight: 900, fontSize: 16 }}>
                  {descProject?.title || 'Chi tiết mô tả'}
                </div>
              </div>

              <button
                type="button"
                onClick={closeDescriptionModal}
                style={{
                  border: 'none',
                  background: '#f3f4f6',
                  borderRadius: 8,
                  padding: '8px 12px',
                  cursor: 'pointer',
                  fontWeight: 800,
                }}
              >
                Đóng
              </button>
            </div>

            <div style={{ padding: 16 }}>
              <div style={{ fontWeight: 800, marginBottom: 8 }}>Mô tả / Đề cương</div>
              <pre
                style={{
                  margin: 0,
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-word',
                  background: '#f9fafb',
                  border: '1px solid #eee',
                  borderRadius: 10,
                  padding: 14,
                  maxHeight: '60vh',
                  overflowY: 'auto',
                  lineHeight: 1.5,
                  fontFamily: 'inherit',
                  fontSize: 14,
                }}
              >
                {descProject?.description || '(Không có mô tả)'}
              </pre>
            </div>
          </div>
        </div>
      )}

      <style>{`
        .badge{ padding:4px 10px; border-radius:999px; font-size:12px; font-weight:800; }
        .badge-approved{ background:#dcfce7; }
        .badge-pending{ background:#fef3c7; }
        .badge-denied{ background:#fee2e2; }
        .badge-draft{ background:#f3f4f6; }
      `}</style>
    </div>
  );
};

export default ProjectList;
