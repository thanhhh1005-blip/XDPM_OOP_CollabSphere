import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { Card, Form, Input, Button, message, Space, Typography, Select, Tag } from "antd";
import { ArrowLeftOutlined } from "@ant-design/icons";
import { getAuthInfo } from "../../utils/authStorage";

const { Title, Text } = Typography;

const TeamCreate = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);

  // meta: class + students
  const [classes, setClasses] = useState([]); // [{id, classCode}]
  const [students, setStudents] = useState([]); // [{studentId, fullName, leaderUsed?}]
  const [loadingClasses, setLoadingClasses] = useState(false);
  const [loadingStudents, setLoadingStudents] = useState(false);
  // Logic kiểm tra xem sinh viên đã tham gia bất kỳ nhóm nào chưa
const isStudentAlreadyInTeam = (student) => {
  // Bạn có thể gộp tất cả các điều kiện backend trả về ở đây
  return student.hasTeam || student.leaderUsed || student.isMember; 
};
  // meta: projects
  const [projects, setProjects] = useState([]); // [{id,title,status,assigned}]
  const [loadingProjects, setLoadingProjects] = useState(false);

  // Auth
  const auth = useMemo(() => getAuthInfo() || {}, []);
  const role = auth.role;
  const userId = auth.username;

  const token =
    auth.token ||
    auth.accessToken ||
    localStorage.getItem("token") ||
    localStorage.getItem("accessToken");

  const headers = useMemo(() => {
  // Lấy auth info
  const authInfo = getAuthInfo() || {};
  const role = authInfo.role;
  const userId = authInfo.username;
  const token = authInfo.token || authInfo.accessToken || 
                localStorage.getItem("token") || 
                localStorage.getItem("accessToken");

  // ❌ KHÔNG trả về null nữa - luôn trả về object
  const finalHeaders = {
    "X-ROLE": role || "",
    "X-USER-ID": userId || "",
  };

  // Chỉ thêm Authorization nếu có token
  if (token) {
    finalHeaders.Authorization = `Bearer ${token}`;
  }

  console.log("📋 Headers được tạo:", finalHeaders);
  return finalHeaders;
}, [])

  // ===== API =====
  const GW = "http://localhost:8080";
  const META_CLASSES_API = `${GW}/api/v1/teams/meta/classes`;
  const META_STUDENTS_API = (classId) => `${GW}/api/v1/teams/meta/classes/${classId}/students`;
  const PROJECTS_API = `${GW}/api/v1/projects`;
  const TEAMS_API = `${GW}/api/v1/teams`;

  // =========================
  // 1. Load Classes
  // =========================
  useEffect(() => {
  const loadClasses = async () => {
    try {
      setLoadingClasses(true);
      
      console.log("🚀 Đang gọi API:", META_CLASSES_API);
      console.log("📋 Với headers:", headers);
      
      const res = await axios.get(META_CLASSES_API, { headers });
      
      console.log("✅ Response nhận được:", res.data);
      
      // Xử lý response data
      const data = Array.isArray(res.data) 
        ? res.data 
        : (res.data?.result || []);
      
      console.log("📊 Danh sách lớp đã parse:", data);
      setClasses(data);
      
      if (data.length === 0) {
        message.warning("Không tìm thấy lớp nào. Đảm bảo bạn đã đăng nhập với role LECTURER.");
      }
      
    } catch (e) {
      console.error("❌ Lỗi chi tiết:", e);
      console.error("❌ Response lỗi:", e.response?.data);
      console.error("❌ Status code:", e.response?.status);
      
      if (e.response?.status === 400) {
        message.error("Headers không hợp lệ. Vui lòng đăng nhập lại.");
      } else if (e.response?.status === 401) {
        message.error("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
      } else {
        message.error("Không tải được danh sách lớp: " + (e.message || "Lỗi không xác định"));
      }
    } finally {
      setLoadingClasses(false);
    }
  };

  // Chỉ load khi component mount
  loadClasses();
}, []);

  // =========================
  // 2. Load Projects (Chuẩn hóa logic)
  // =========================
  useEffect(() => {
    const loadProjectsAndCheckAssigned = async () => {
      try {
        setLoadingProjects(true);

        // BƯỚC 1: Lấy tất cả các Team hiện có để xem Project nào đã bị xí phần
        let assignedProjectIds = new Set();
        try {
            const teamRes = await axios.get(TEAMS_API, { headers });
            const teams = Array.isArray(teamRes.data) ? teamRes.data : (teamRes.data?.result || []);
            
            teams.forEach(t => {
                if (t.projectId) {
                    assignedProjectIds.add(String(t.projectId));
                }
            });
        } catch (err) {
            console.warn("Không tải được danh sách Team để check trùng project:", err);
        }

        // BƯỚC 2: Gọi API lấy Project với tham số lọc status=APPROVED
        // 🔥 QUAN TRỌNG: Chỉ lấy dự án đã duyệt, Server sẽ lọc giùm ta
        const projectRes = await axios.get(PROJECTS_API, { 
            headers,
            params: { status: 'APPROVED' } 
        });

        const rawProjects = Array.isArray(projectRes.data) ? projectRes.data : (projectRes.data?.result || []);

        // BƯỚC 3: Map dữ liệu và đánh dấu "Đã gán"
        const mappedProjects = rawProjects.map(p => ({
            ...p,
            assigned: assignedProjectIds.has(String(p.id)) // True nếu ID này đã nằm trong danh sách team
        }));

        setProjects(mappedProjects);

      } catch (e) {
        console.error(e);
        // message.error("Lỗi khi tải dữ liệu dự án");
      } finally {
        setLoadingProjects(false);
      }
    };

    loadProjectsAndCheckAssigned();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // =========================
  // When choose class -> load students
  // =========================
  const onChangeClass = async (classId) => {
    form.setFieldsValue({ leaderId: undefined, memberIds: [] });
    setStudents([]);

    if (!classId) return;

    try {
      setLoadingStudents(true);
      const res = await axios.get(META_STUDENTS_API(classId), { headers });
      const data = Array.isArray(res.data) ? res.data : (res.data?.result || []);
      setStudents(data);
    } catch (e) {
      console.error(e);
      message.error("Không tải được danh sách sinh viên");
    } finally {
      setLoadingStudents(false);
    }
  };

  // =========================
  // Submit
  // =========================
  const onFinish = async (values) => {
    try {
      setSubmitting(true);

      // 1. Chuẩn bị dữ liệu (Payload) chuẩn JSON
      const payload = {
        name: values.name?.trim(),
        classId: values.classId, // Số nguyên hoặc chuỗi đều OK
        projectId: values.projectId ? String(values.projectId).trim() : null, // Gửi null nếu không chọn
        leaderId: values.leaderId ? String(values.leaderId).trim() : null,
        memberIds: Array.isArray(values.memberIds) ? values.memberIds : [] // Gửi mảng trực tiếp
      };

      // 2. Gửi Request POST
      // Cú pháp: axios.post(URL, BODY, CONFIG)
      await axios.post(`${GW}/api/v1/teams`, payload, {
        headers, 
        // ❌ KHÔNG DÙNG params NỮA
      });

      message.success("Tạo team thành công!");
      navigate("/teams");
    } catch (err) {
      console.error(err);
      message.error(err.response?.data?.message || "Tạo team thất bại");
    } finally {
      setSubmitting(false);
    }
  };

  // Helper render status
  const renderStatusTag = (status) => {
    if (!status) return null;
    if (status === "APPROVED") return <Tag color="green">AVAILABLE</Tag>; // Hiển thị Available cho thân thiện
    return <Tag>{status}</Tag>;
  };

  return (
    <div style={{ maxWidth: 900, margin: "0 auto", paddingBottom: 40 }}>
      <Space direction="vertical" size={16} style={{ width: "100%" }}>
        {/* Header */}
        <div style={{ display: "flex", alignItems: "flex-start", gap: 12 }}>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate("/teams")}>
            Quay lại
          </Button>

          <div>
            <Title level={3} style={{ margin: 0 }}>
              Tạo Team Mới
            </Title>
            <Text type="secondary">
              Tạo nhóm, thêm thành viên và đăng ký đề tài (Chỉ đề tài đã được duyệt)
            </Text>
          </div>
        </div>

        {/* Form Card */}
        <Card style={{ borderRadius: 12, boxShadow: "0 4px 12px rgba(0,0,0,0.05)" }} bodyStyle={{ padding: 24 }}>
          <Form
            form={form}
            layout="vertical"
            onFinish={onFinish}
            requiredMark={false}
            autoComplete="off"
          >
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16 }}>
              <Form.Item
                label={<span style={{ fontWeight: 600 }}>Tên team</span>}
                name="name"
                rules={[
                  { required: true, message: "Vui lòng nhập tên team" },
                  { min: 2, message: "Tên team quá ngắn" },
                ]}
              >
                <Input placeholder="Ví dụ: Nhóm 1 - KTPM" size="large" />
              </Form.Item>

              {/* Class */}
              <Form.Item
                label={<span style={{ fontWeight: 600 }}>Lớp học phần</span>}
                name="classId"
                rules={[{ required: true, message: "Vui lòng chọn lớp" }]}
              >
                <Select
                  placeholder="Chọn lớp..."
                  size="large"
                  loading={loadingClasses}
                  showSearch
                  optionFilterProp="label"
                  options={classes.map((c) => ({
                    value: c.id,
                    label: c.className ? `${c.classCode} - ${c.className}` : c.classCode,
                  }))}
                  onChange={onChangeClass}
                />
              </Form.Item>

              {/* Leader */}
              <Form.Item label={<span style={{ fontWeight: 600 }}>Trưởng nhóm (Leader)</span>} name="leaderId">
                <Select
                  placeholder="Chọn trưởng nhóm..."
                  size="large"
                  loading={loadingStudents}
                  disabled={!form.getFieldValue("classId")}
                  showSearch
                  optionFilterProp="label"
                  options={students.map((s) => {
                      // Logic tương tự: Đã có nhóm (dù vai trò gì) thì không được làm leader nhóm mới
                      const isBusy = s.hasTeam || s.leaderUsed; 
                      return {
                          value: s.studentId,
                          label: `${s.id || s.studentId} - ${s.fullName}`,
                          disabled: isBusy, // 👈 Disable
                      };
                  })}
                  optionRender={(option) => (
                      <div style={{ display: "flex", justifyContent: "space-between" }}>
                          <span>{option.label}</span>
                          {option.data.disabled && <Tag color="default">Đã có nhóm</Tag>}
                      </div>
                  )}
                />
              </Form.Item>

              {/* Members */}
              <Form.Item
                label={<span style={{ fontWeight: 600 }}>Thành viên</span>}
                name="memberIds"
                rules={[{ required: true, message: "Chọn ít nhất 1 thành viên" }]}
              >
                <Select
                  mode="multiple"
                  placeholder="Chọn các thành viên..."
                  size="large"
                  loading={loadingStudents}
                  disabled={!form.getFieldValue("classId")}
                  showSearch
                  optionFilterProp="label"
                  // 👇 LOGIC QUAN TRỌNG Ở ĐÂY
                  options={students.map((s) => {
                      // Check xem sinh viên đã có nhóm chưa (dựa vào cờ backend trả về)
                      // Nếu backend trả về leaderUsed và memberUsed riêng, hãy gộp lại:
                      // const isBusy = s.leaderUsed || s.memberUsed || s.hasTeam;
                      
                      // Giả sử backend trả về field 'hasTeam' (đã bao gồm cả leader và member)
                      const isBusy = s.hasTeam || s.leaderUsed; 

                      return {
                          value: s.studentId,
                          label: `${s.id || s.studentId} - ${s.fullName}`,
                          disabled: isBusy, // 👈 Disable nếu đã có nhóm
                          isBusy: isBusy // Lưu prop này để dùng lúc render custom nếu cần
                      };
                  })}
                  // 👇 (Tùy chọn) Custom hiển thị để người dùng biết tại sao bị mờ
                  optionRender={(option) => (
                      <div style={{ display: "flex", justifyContent: "space-between" }}>
                          <span>{option.label}</span>
                          {option.data.disabled && <Tag color="default">Đã có nhóm</Tag>}
                      </div>
                  )}
                />
              </Form.Item>

              {/* ✅ PROJECT SELECT (ĐÃ LỌC CHUẨN) */}
              <Form.Item 
                label={<span style={{ fontWeight: 600 }}>Đề tài / Dự án (Project)</span>} 
                name="projectId" 
                style={{ gridColumn: "1 / -1" }}
                // 👇 ĐƯA DÒNG CHÚ THÍCH VÀO ĐÂY
                extra={<span style={{ fontSize: 12, color: '#666' }}>* Chỉ hiển thị các đề tài đã được Trưởng bộ môn phê duyệt (APPROVED).</span>}
              >
                {/* 👇 BÊN TRONG CHỈ ĐƯỢC ĐỂ DUY NHẤT 1 CÁI SELECT */}
                <Select
                  placeholder="Chọn đề tài đã được phê duyệt..."
                  size="large"
                  loading={loadingProjects}
                  allowClear
                  showSearch
                  optionFilterProp="label"
                  options={projects.map((p) => ({
                    value: p.id,
                    label: `${p.title} (${p.projectCode || 'Mới'})`,
                    disabled: !!p.assigned,
                  }))}
                  optionRender={(option) => {
                    const p = projects.find((x) => String(x.id) === String(option.value));
                    if (!p) return option.label;

                    return (
                      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "4px 0" }}>
                        <div style={{ display:"flex", flexDirection:"column" }}>
                            <span style={{ fontWeight: 600, color: p.assigned ? "#999" : "#000" }}>{p.title}</span>
                            <span style={{ fontSize: 12, color: "#666" }}>{p.projectCode}</span>
                        </div>
                        
                        <div>
                          {p.assigned ? (
                             <Tag color="error">ĐÃ CÓ NHÓM</Tag>
                          ) : (
                             <Tag color="success">KHẢ DỤNG</Tag>
                          )}
                        </div>
                      </div>
                    );
                  }}
                />
              </Form.Item>
            </div>

            <div style={{ display: "flex", justifyContent: "flex-end", gap: 12, marginTop: 24 }}>
              <Button size="large" onClick={() => navigate("/teams")}>Hủy bỏ</Button>
              <Button
                type="primary"
                htmlType="submit"
                size="large"
                loading={submitting}
                disabled={role !== "LECTURER"}
              >
                Tạo Team
              </Button>
            </div>

            {role !== "LECTURER" && (
              <div style={{ marginTop: 16, textAlign: 'center' }}>
                <Text type="danger">
                  Bạn đang đăng nhập với quyền <b>{role}</b>. Chỉ <b>LECTURER</b> mới có quyền tạo nhóm.
                </Text>
              </div>
            )}
          </Form>
        </Card>
      </Space>
    </div>
  );
};

export default TeamCreate;