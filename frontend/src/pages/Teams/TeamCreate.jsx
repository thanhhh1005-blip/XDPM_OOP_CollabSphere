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

  // meta: projects
  const [projects, setProjects] = useState([]); // [{id,title,status,assigned}]
  const [loadingProjects, setLoadingProjects] = useState(false);

  const auth = useMemo(() => getAuthInfo() || {}, []);
  const role = auth.role;
  const userId = auth.userId;

  const token =
    auth.token ||
    auth.accessToken ||
    localStorage.getItem("token") ||
    localStorage.getItem("accessToken");

  const headers = useMemo(
    () => ({
      "X-ROLE": role,
      "X-USER-ID": userId,
      Authorization: token ? `Bearer ${token}` : undefined,
    }),
    [role, userId, token]
  );

  // ===== API =====
  const GW = "http://localhost:8080";

  // team meta
  const META_CLASSES_API = `${GW}/api/v1/teams/meta/classes`;
  const META_STUDENTS_API = (classId) => `${GW}/api/v1/teams/meta/classes/${classId}/students`;

  // projects from project-service (qua gateway)
  const PROJECTS_API = `${GW}/api/v1/projects`;

  // teams from team-service (để check projectId đã gán)
  const TEAMS_API = `${GW}/api/v1/teams`;

  // =========================
  // Load Classes
  // =========================
  useEffect(() => {
    const loadClasses = async () => {
      try {
        setLoadingClasses(true);
        const res = await axios.get(META_CLASSES_API, { headers });
        setClasses(Array.isArray(res.data) ? res.data : []);
      } catch (e) {
        console.error(e);
        message.error("Không tải được danh sách lớp (class-service)");
      } finally {
        setLoadingClasses(false);
      }
    };
    loadClasses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // =========================
  // Load Projects + mark assigned
  // =========================
  useEffect(() => {
    const loadProjects = async () => {
      try {
        setLoadingProjects(true);

        // 1) lấy projects
        const prRes = await axios.get(PROJECTS_API, { headers });
        const allProjects = Array.isArray(prRes.data) ? prRes.data : [];

        // 2) lấy teams để check projectId đã gán
        const tRes = await axios.get(TEAMS_API, { headers });
        const allTeams = Array.isArray(tRes.data) ? tRes.data : [];

        const assignedSet = new Set(
          allTeams
            .map((t) => (t?.projectId ? String(t.projectId) : null))
            .filter(Boolean)
        );

        // 3) chỉ show APPROVED (tuỳ bạn muốn show tất cả thì bỏ filter)
        const mapped = allProjects
          .filter((p) => !p?.status || p.status === "APPROVED")
          .map((p) => ({
            ...p,
            assigned: assignedSet.has(String(p.id)),
          }));

        setProjects(mapped);
      } catch (e) {
        console.error(e);
        message.error("Không tải được danh sách project (project-service) hoặc teams (team-service)");
      } finally {
        setLoadingProjects(false);
      }
    };

    loadProjects();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // =========================
  // When choose class -> load students
  // =========================
  const onChangeClass = async (classId) => {
    // reset leader/members
    form.setFieldsValue({ leaderId: undefined, memberIds: [] });
    setStudents([]);

    if (!classId) return;

    try {
      setLoadingStudents(true);
      const res = await axios.get(META_STUDENTS_API(classId), { headers });
      setStudents(Array.isArray(res.data) ? res.data : []);
    } catch (e) {
      console.error(e);
      message.error("Không tải được danh sách sinh viên của lớp");
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

      const params = new URLSearchParams();
      params.append("name", values.name?.trim());
      params.append("classId", String(values.classId));

      const projectId = values.projectId ? String(values.projectId).trim() : "";
      if (projectId) params.append("projectId", projectId);

      const leaderId = values.leaderId?.trim();
      if (leaderId) params.append("leaderId", leaderId);

      const memberIds = Array.isArray(values.memberIds) ? values.memberIds : [];
      memberIds.forEach((m) => params.append("memberIds", m));

      await axios.post(`${GW}/api/v1/teams`, null, {
        headers,
        params, // memberIds=...&memberIds=...
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

  // helper: render status tag
  const renderStatusTag = (status) => {
    if (!status) return null;
    if (status === "APPROVED") return <Tag color="green">APPROVED</Tag>;
    if (status === "PENDING") return <Tag color="gold">PENDING</Tag>;
    if (status === "DENIED") return <Tag color="red">DENIED</Tag>;
    return <Tag>{status}</Tag>;
  };

  return (
    <div style={{ maxWidth: 900, margin: "0 auto" }}>
      <Space direction="vertical" size={16} style={{ width: "100%" }}>
        {/* Header */}
        <div style={{ display: "flex", alignItems: "flex-start", gap: 12 }}>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate("/teams")}>
            Quay lại
          </Button>

          <div>
            <Title level={3} style={{ margin: 0 }}>
              Tạo Team
            </Title>
            <Text type="secondary">
              Chọn lớp & thành viên từ class-service, chọn project từ project-service (không hiện UUID)
            </Text>
          </div>
        </div>

        {/* Form Card */}
        <Card style={{ borderRadius: 12 }} bodyStyle={{ padding: 24 }}>
          <Form
            form={form}
            layout="vertical"
            onFinish={onFinish}
            requiredMark={false}
            autoComplete="off"
          >
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr",
                gap: 16,
              }}
            >
              <Form.Item
                label="Tên team"
                name="name"
                rules={[
                  { required: true, message: "Vui lòng nhập tên team" },
                  { min: 2, message: "Tên team tối thiểu 2 ký tự" },
                ]}
              >
                <Input placeholder="VD: Team 1" />
              </Form.Item>

              {/* Class */}
              <Form.Item
                label="Mã lớp"
                name="classId"
                rules={[{ required: true, message: "Vui lòng chọn lớp" }]}
              >
                <Select
                  placeholder="Chọn lớp"
                  loading={loadingClasses}
                  showSearch
                  optionFilterProp="label"
                  options={classes.map((c) => ({
                    value: c.id,
                    label: c.classCode,
                  }))}
                  onChange={onChangeClass}
                />
              </Form.Item>

              {/* Leader */}
              <Form.Item label="Trưởng nhóm (tuỳ chọn)" name="leaderId">
                <Select
                  placeholder="Chọn sinh viên làm trưởng nhóm"
                  loading={loadingStudents}
                  disabled={!form.getFieldValue("classId")}
                  showSearch
                  optionFilterProp="label"
                  options={students.map((s) => ({
                    value: s.studentId,
                    label: `${s.id} - ${s.fullName}`,
                    disabled: !!s.leaderUsed,
                  }))}
                />
              </Form.Item>

              {/* Members */}
              <Form.Item
                label="Thành viên nhóm"
                name="memberIds"
                rules={[{ required: true, message: "Vui lòng chọn ít nhất 1 thành viên" }]}
              >
                <Select
                  mode="multiple"
                  allowClear
                  placeholder="Chọn thành viên trong lớp"
                  loading={loadingStudents}
                  disabled={!form.getFieldValue("classId")}
                  showSearch
                  optionFilterProp="label"
                  options={students.map((s) => ({
                    value: s.studentId,
                    label: `${s.id} - ${s.fullName || s.studentId}`,
                  }))}
                />
              </Form.Item>

              {/* ✅ Project Select (NO UUID DISPLAY) */}
              <Form.Item label="Project (tuỳ chọn)" name="projectId">
                <Select
                  placeholder="Chọn project từ Project Service"
                  loading={loadingProjects}
                  allowClear
                  showSearch
                  optionFilterProp="label"
                  options={projects.map((p) => ({
                    value: p.id, // ✅ vẫn là UUID để lưu
                    label: `${p.title} • ${p.status}`, // ✅ KHÔNG hiện id
                    disabled: !!p.assigned, // ✅ đã gán thì disable
                  }))}
                  optionRender={(option) => {
                    const p = projects.find((x) => String(x.id) === String(option.value));
                    if (!p) return option.label;

                    return (
                      <div style={{ display: "flex", justifyContent: "space-between", gap: 12 }}>
                        <div style={{ fontWeight: 800 }}>{p.title}</div>
                        <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
                          {renderStatusTag(p.status)}
                          {p.assigned && (
                            <span style={{ color: "red", fontWeight: 800 }}>Đã gán</span>
                          )}
                        </div>
                      </div>
                    );
                  }}
                />
              </Form.Item>
            </div>

            <div style={{ display: "flex", justifyContent: "flex-end", gap: 12 }}>
              <Button onClick={() => navigate("/teams")}>Hủy</Button>
              <Button
                type="primary"
                htmlType="submit"
                loading={submitting}
                disabled={role !== "LECTURER"}
              >
                Tạo
              </Button>
            </div>

            {role !== "LECTURER" && (
              <div style={{ marginTop: 12 }}>
                <Text type="danger">
                  Bạn đang đăng nhập role {role || "?"}. Chỉ LECTURER mới tạo được team.
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
