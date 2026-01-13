import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { Alert, Button, Card, Descriptions, Space, Spin, Typography } from "antd";
import { ArrowLeftOutlined } from "@ant-design/icons";
import { getAuthInfo } from "../../utils/authStorage";

const { Title, Text } = Typography;

function formatISO(iso) {
  if (!iso) return "—";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleString("vi-VN", { hour12: false });
}

export default function TeamDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [team, setTeam] = useState(null);
  const [classLabel, setClassLabel] = useState("—");   // ✅ mã lớp
  const [leaderLabel, setLeaderLabel] = useState("—"); // ✅ studentId nếu match
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const auth = getAuthInfo() || {};
  const { role, userId } = auth;

  const headers = useMemo(() => {
    const token =
      auth.token ||
      auth.accessToken ||
      localStorage.getItem("token") ||
      localStorage.getItem("accessToken");

    return {
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(role ? { "X-ROLE": role } : {}),
      ...(userId ? { "X-USER-ID": String(userId) } : {}),
    };
  }, [auth, role, userId]);

  const TEAM_API = `http://localhost:8080/api/v1/teams/${id}`;
  const META_CLASSES_API = `http://localhost:8080/api/v1/teams/meta/classes`;

  const fetchDetail = async () => {
    try {
      setLoading(true);
      setError("");

      // 1) load team detail
      const res = await axios.get(TEAM_API, { headers });
      const t = res.data?.result ?? res.data;
      setTeam(t);

      // 2) load class meta => map classId -> code
      try {
        const cRes = await axios.get(META_CLASSES_API, { headers });
        const raw = cRes.data?.result ?? cRes.data ?? [];
        const classes = Array.isArray(raw) ? raw : [];

        const c = classes.find((x) => String(x.id) === String(t.classId));
        const code = c?.code ?? c?.classCode ?? c?.class_code;

        setClassLabel(code || "—");
      } catch (e) {
        setClassLabel("—");
      }

      // 3) load students of that class => if leaderId matches a studentId, show it
      try {
        if (t?.classId) {
          const stuRes = await axios.get(
            `http://localhost:8080/api/v1/teams/meta/classes/${t.classId}/students`,
            { headers }
          );
          const rawStu = stuRes.data?.result ?? stuRes.data ?? [];
          const students = Array.isArray(rawStu) ? rawStu : [];

          const found = students.find(
            (s) => String(s.studentId) === String(t.leaderId)
          );
          setLeaderLabel(found ? found.studentId : (t.leaderId || "—"));
        } else {
          setLeaderLabel(t?.leaderId || "—");
        }
      } catch (e) {
        setLeaderLabel(t?.leaderId || "—");
      }
    } catch (e) {
      console.error(e);
      setError(e?.response?.data?.message || "Không thể tải chi tiết team");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDetail();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  if (loading) {
    return (
      <div style={{ padding: 40, textAlign: "center" }}>
        <Spin size="large" />
        <div style={{ marginTop: 12 }}>Đang tải chi tiết team...</div>
      </div>
    );
  }

  if (error) {
    return (
      <Alert
        type="error"
        message="Lỗi"
        description={error}
        showIcon
        action={
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate("/teams")}>
              Quay lại
            </Button>
            <Button type="primary" onClick={fetchDetail}>
              Thử lại
            </Button>
          </Space>
        }
      />
    );
  }

  if (!team) {
    return (
      <Alert
        type="warning"
        message="Không tìm thấy team"
        showIcon
        action={
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate("/teams")}>
            Quay lại
          </Button>
        }
      />
    );
  }

  return (
    <Card style={{ borderRadius: 16 }} styles={{ body: { padding: 20 } }}>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "flex-start",
          gap: 16,
        }}
      >
        <div>
          <Title level={3} style={{ margin: 0 }}>
            {team.name || "—"}
          </Title>
          <Text type="secondary">Thông tin chi tiết team</Text>
        </div>

        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate("/teams")}>
          Quay lại
        </Button>
      </div>

      <div style={{ marginTop: 16 }}>
        <Descriptions bordered column={1} size="middle">
          <Descriptions.Item label="Lớp">{classLabel || "—"}</Descriptions.Item>
          <Descriptions.Item label="Dự án">{team.projectId || "—"}</Descriptions.Item>
          <Descriptions.Item label="Trưởng nhóm">
            {leaderLabel || team.leaderId || "—"}
          </Descriptions.Item>
          <Descriptions.Item label="Ngày tạo">{formatISO(team.createdAt)}</Descriptions.Item>
        </Descriptions>
      </div>
    </Card>
  );
}
