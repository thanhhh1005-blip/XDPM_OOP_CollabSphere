import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getAuthInfo } from "../../utils/authStorage";
import { Card, Button, Table, Space, Typography, Alert, Popconfirm, message } from "antd";
import { PlusOutlined } from "@ant-design/icons";

const { Title, Text } = Typography;

const TeamList = () => {
  const navigate = useNavigate();

  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // ✅ map classId -> { code, semester }
  const [classMap, setClassMap] = useState({});

  const auth = useMemo(() => getAuthInfo() || {}, []);
  const role = auth.role;
  const userId = auth.userId;

  const token = useMemo(() => {
    return (
      auth.token ||
      auth.accessToken ||
      localStorage.getItem("token") ||
      localStorage.getItem("accessToken")
    );
  }, [auth]);

  const headers = useMemo(
    () => ({
      "X-ROLE": role,
      "X-USER-ID": userId,
      Authorization: token ? `Bearer ${token}` : undefined,
    }),
    [role, userId, token]
  );

  const TEAMS_API = "http://localhost:8080/api/v1/teams";
  const META_CLASSES_API = "http://localhost:8080/api/v1/teams/meta/classes";

  const fetchTeams = useCallback(async () => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const token = localStorage.getItem('token');
    const config = { headers: { Authorization: `Bearer ${token}` } };

    try {
        setLoading(true);
        let url = "http://localhost:8080/api/v1/teams"; // Mặc định cho Admin

        if (user.role === 'STUDENT') {
            // Nếu là SV: Chỉ lấy team mình tham gia
            url = `http://localhost:8080/api/v1/teams/student/${user.username}`;
        } 
        else if (user.role === 'LECTURER') {
            // 👇 SỬA CHỖ NÀY: Nếu là GV: Chỉ lấy team thuộc các lớp mình dạy
            url = `http://localhost:8080/api/v1/teams/lecturer/${user.username}`;
        }

        const res = await axios.get(url, config);
        const teamData = res.data?.result ?? res.data;
        setTeams(Array.isArray(teamData) ? teamData : []);
    } catch (error) {
        console.error(error);
        message.error("Không thể tải danh sách nhóm");
    } finally {
        setLoading(false);
    }
}, []);

  const fetchClassesMap = useCallback(async () => {
    try {
      const res = await axios.get(META_CLASSES_API, { headers });

      // endpoint có thể trả [] hoặc { result: [] }
      const raw = res.data?.result ?? res.data ?? [];
      const data = Array.isArray(raw) ? raw : [];

      const map = {};
      data.forEach((c) => {
        const id = c.id ?? c.classId;
        const code = c.code ?? c.classCode ?? c.class_code; // ✅ ưu tiên code
        const semester = c.semester ?? c.className ?? c.semesterName;

        if (id != null) {
          map[String(id)] = { code, semester };
        }
      });

      setClassMap(map);
    } catch (e) {
      console.error(e);
      setClassMap({});
    }
  }, [headers]);

  useEffect(() => {
    fetchTeams();
    fetchClassesMap();
  }, [fetchTeams, fetchClassesMap]);

  // ✅ chỉ hiển thị MÃ LỚP (code). Không bao giờ trả undefined.
  const renderClassCode = (classId) => {
    if (
      classId === null ||
      classId === undefined ||
      classId === 0 ||
      String(classId) === "0"
    )
      return "—";

    const c = classMap[String(classId)];
    const code = c?.code;

    if (!code) return "—";
    return code;
  };

  const handleDelete = async (teamId) => {
    try {
      await axios.delete(`http://localhost:8080/api/v1/teams/${teamId}`, { headers });
      message.success("Đã xóa team");
      fetchTeams();
    } catch (e) {
      console.error(e);
      message.error(e.response?.data?.message || "Xóa team thất bại");
    }
  };

  const columns = [
    {
      title: "Tên team",
      dataIndex: "name",
      key: "name",
      render: (v) => <Text strong>{v}</Text>,
    },
    {
      title: "Lớp",
      dataIndex: "classId",
      key: "classId",
      width: 220,
      render: (v) => renderClassCode(v), // ✅ luôn CNxx hoặc —
    },
    {
      title: "Trưởng nhóm",
      dataIndex: "leaderId",
      key: "leaderId",
      width: 220,
      render: (v) => v || "—",
    },
    {
      title: "Hành động",
      key: "action",
      width: 220,
      render: (_, t) => (
        <Space>
          <Button onClick={() => navigate(`/teams/${t.id}`)}>Xem</Button>

          <Button onClick={() => navigate(`/teams/${t.id}/edit`)} disabled={role !== "LECTURER"}>
            Sửa
          </Button>

          <Popconfirm
            title="Xóa team này?"
            description="Hành động không thể hoàn tác."
            okText="Xóa"
            cancelText="Hủy"
            onConfirm={() => handleDelete(t.id)}
            disabled={role !== "LECTURER"}
          >
            <Button danger disabled={role !== "LECTURER"}>Xóa</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ maxWidth: 1100, margin: "0 auto" }}>
      <Card style={{ borderRadius: 12 }} styles={{ body: { padding: 20 } }}>
        <div
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            gap: 12,
            marginBottom: 12,
          }}
        >
          <div>
            <Title level={4} style={{ margin: 0 }}>
              Danh sách Team
            </Title>
            <Text type="secondary">Quản lý các team theo lớp</Text>
          </div>

          <Space>
            {role === "LECTURER" && (
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => navigate("/teams/new")}
              >
                Tạo Team
              </Button>
            )}
          </Space>
        </div>

        {error && (
          <Alert
            type="error"
            showIcon
            message="Lỗi"
            description={error}
            style={{ marginBottom: 12 }}
          />
        )}

        <Table
          rowKey="id"
          columns={columns}
          dataSource={teams}
          loading={loading}
          pagination={{ pageSize: 8 }}
        />
      </Card>
    </div>
  );
};

export default TeamList;
