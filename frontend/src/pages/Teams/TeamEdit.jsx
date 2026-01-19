import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { Card, Form, Input, Button, Select, message, Space } from "antd";
import { getAuthInfo } from "../../utils/authStorage";

export default function TeamEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form] = Form.useForm();

  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);

  const auth = getAuthInfo() || {};
  const token = auth.token || localStorage.getItem("token");

  const headers = {
    Authorization: token ? `Bearer ${token}` : undefined,
    "X-ROLE": auth.role,
  };

  useEffect(() => {
    const load = async () => {
      setLoading(true);

      try {
        // 1️⃣ load team
        const teamRes = await axios.get(
          `http://localhost:8080/api/v1/teams/${id}`,
          { headers }
        );
        const team = teamRes.data?.result ?? teamRes.data;

        // 2️⃣ load students
        const stuRes = await axios.get(
          `http://localhost:8080/api/v1/teams/meta/classes/${team.classId}/students`,
          { headers }
        );

        setStudents(stuRes.data?.result ?? stuRes.data);

        // 3️⃣ set form
        form.setFieldsValue({
          name: team.name,
          leaderId: team.leaderId,
          memberIds: team.memberIds || [],
        });
      } catch (e) {
        console.error(e);
        message.error(e?.response?.data?.message || "Không tải được dữ liệu team");
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [id]);

  const onFinish = async (values) => {
    try {
      const params = new URLSearchParams();
      params.append("name", values.name);

      if (values.leaderId) params.append("leaderId", values.leaderId);

      (values.memberIds || []).forEach((m) => params.append("memberIds", m));

      await axios.put(
        `http://localhost:8080/api/v1/teams/${id}`,
        null,
        {
          headers,
          params,
        }
      );

      message.success("Cập nhật team thành công");
      navigate(`/teams/${id}`);
    } catch (e) {
      console.error(e);
      message.error(e?.response?.data?.message || "Cập nhật thất bại");
    }
  };

  return (
    <Card title="Sửa Team">
      <Form form={form} layout="vertical" onFinish={onFinish}>
        <Form.Item
          name="name"
          label="Tên team"
          rules={[{ required: true, message: "Vui lòng nhập tên team" }]}
        >
          <Input />
        </Form.Item>

        <Form.Item name="leaderId" label="Trưởng nhóm">
          <Select
            options={students.map(s => ({
              value: s.studentId,
              label: s.studentId,
              disabled: !!s.leaderUsed && String(s.studentId) !== String(form.getFieldValue("leaderId")),
            }))}
          />
        </Form.Item>

        <Form.Item name="memberIds" label="Thành viên nhóm">
          <Select
            mode="multiple"
            options={students.map(s => ({
              value: s.studentId,
              label: s.studentId,
            }))}
          />
        </Form.Item>

        <Space>
          <Button onClick={() => navigate("/teams")}>
            Hủy
          </Button>

          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
            disabled={loading || (auth.role && auth.role !== "LECTURER")}
          >
            Lưu
          </Button>
        </Space>
      </Form>
    </Card>
  );
}
