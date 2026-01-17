import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { Card, Form, Input, Button, message, Space, Typography, Select } from "antd";
import { ArrowLeftOutlined } from "@ant-design/icons";
import { getAuthInfo } from "../../utils/authStorage";

const { Title, Text } = Typography;

const TeamCreate = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);

  // meta
  const [classes, setClasses] = useState([]); // [{id, classCode}]
  const [students, setStudents] = useState([]); // [{studentId}]
  const [loadingClasses, setLoadingClasses] = useState(false);
  const [loadingStudents, setLoadingStudents] = useState(false);

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

  const META_CLASSES_API = "http://localhost:8080/api/v1/teams/meta/classes";

  // Load lớp
  useEffect(() => {
    const loadClasses = async () => {
      try {
        setLoadingClasses(true);
        const res = await axios.get(META_CLASSES_API, { headers });
        const data = Array.isArray(res.data) ? res.data : [];
        setClasses(data);
      } catch (e) {
        console.error(e);
        message.error("Không tải được danh sách lớp (class-service DB)");
      } finally {
        setLoadingClasses(false);
      }
    };
    loadClasses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Khi chọn lớp -> load students của lớp
  const onChangeClass = async (classId) => {
    // reset leader
    form.setFieldsValue({ leaderId: undefined, memberIds: [] });
    setStudents([]);

    if (!classId) return;

    try {
      setLoadingStudents(true);
      const res = await axios.get(
        `http://localhost:8080/api/v1/teams/meta/classes/${classId}/students`,
        { headers }
      );
      const data = Array.isArray(res.data) ? res.data : [];
      setStudents(data);
    } catch (e) {
      console.error(e);
      message.error("Không tải được danh sách sinh viên của lớp");
    } finally {
      setLoadingStudents(false);
    }
  };

  const onFinish = async (values) => {
    try {
      setSubmitting(true);

      const params = new URLSearchParams();
      params.append("name", values.name?.trim());
      params.append("classId", String(values.classId));

      const projectId = values.projectId?.trim();
      if (projectId) params.append("projectId", projectId);

      const leaderId = values.leaderId?.trim();
      if (leaderId) params.append("leaderId", leaderId);

      const memberIds = Array.isArray(values.memberIds) ? values.memberIds : [];
      memberIds.forEach((m) => params.append("memberIds", m));

      await axios.post("http://localhost:8080/api/v1/teams", null, {
        headers,
        params, // ✅ gửi memberIds=...&memberIds=... (không phải memberIds[])
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
            <Text type="secondary">Chọn lớp và trưởng nhóm từ dữ liệu class-service</Text>
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

              {/* ✅ chọn classId (Long) nhưng hiển thị classCode */}
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
                    value: c.id, // ✅ Long
                    label: c.classCode,
                  }))}
                  onChange={onChangeClass}
                />
              </Form.Item>

              {/* ✅ chọn trưởng nhóm theo lớp */}
              <Form.Item label="Trưởng nhóm (tuỳ chọn)" name="leaderId">
                <Select
                  placeholder="Chọn sinh viên làm trưởng nhóm"
                  loading={loadingStudents}
                  disabled={!form.getFieldValue("classId")}
                  showSearch
                  optionFilterProp="label"
                  options={students.map((s) => ({
                    value: s.studentId, // ✅ gửi sv011
                    label: `${s.id} - ${s.fullName}`, // ✅ hiển thị tên
                    disabled: !!s.leaderUsed,
                  }))}
                />
              </Form.Item>

              {/* ✅ chọn thành viên nhóm theo lớp */}
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
                    label: `${s.id} - ${s.fullName || s.studentId}`, // ✅ bỏ ngoặc
                  }))}
                />
              </Form.Item>

              <Form.Item label="Project ID (tuỳ chọn)" name="projectId">
                <Input placeholder="VD: PR01 (có thể để trống)" />
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
