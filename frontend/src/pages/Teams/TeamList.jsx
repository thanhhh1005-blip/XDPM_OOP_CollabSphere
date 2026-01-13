import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getAuthInfo } from "../../utils/authStorage";
import { Card, Button, Table, Space, Typography, Alert } from "antd";
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
    try {
      setLoading(true);
      setError(null);

      const res = await axios.get(TEAMS_API, { headers });
      const data = res.data?.result ?? res.data ?? [];
      setTeams(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Không thể tải danh sách team");
    } finally {
      setLoading(false);
    }
  }, [headers]);

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
      width: 140,
      render: (_, t) => (
        <Button onClick={() => navigate(`/teams/${t.id}`)}>Xem</Button>
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