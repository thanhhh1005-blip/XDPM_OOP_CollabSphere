import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams, useOutletContext } from "react-router-dom"; // ✅ Thêm useOutletContext
import axios from "axios";
import {
  Alert,
  Button,
  Card,
  Descriptions,
  Space,
  Spin,
  Typography,
  Table,
  Tag,
  message,
  Divider,
} from "antd";
import { ArrowLeftOutlined, StarFilled } from "@ant-design/icons";
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

  // --- GIỮ NGUYÊN TOÀN BỘ STATE CŨ CỦA EM ---
  const [team, setTeam] = useState(null);
  const [classLabel, setClassLabel] = useState("—"); 
  const [leaderLabel, setLeaderLabel] = useState("—"); 
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [members, setMembers] = useState([]); 
  const [loadingMembers, setLoadingMembers] = useState(false); 
  const [projectTitle, setProjectTitle] = useState("—");
  const [loadingProject, setLoadingProject] = useState(false);

  // --- 👇 THÊM LOGIC PHÂN ROLE LINH HOẠT TẠI ĐÂY ---
  const [myRoleInTeam, setMyRoleInTeam] = useState(null); 
  const auth = getAuthInfo() || {};
  const { role: globalRole, userId } = auth; // role từ hệ thống (STUDENT/LECTURER)

  const headers = useMemo(() => {
    const token =
      auth.token ||
      auth.accessToken ||
      localStorage.getItem("token") ||
      localStorage.getItem("accessToken");

    return {
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(globalRole ? { "X-ROLE": globalRole } : {}),
      ...(userId ? { "X-USER-ID": String(userId) } : {}),
    };
  }, [auth, globalRole, userId]);

  const TEAM_API = `http://localhost:8080/api/v1/teams/${id}`;
  const META_CLASSES_API = `http://localhost:8080/api/v1/teams/meta/classes`;
  const TEAM_MEMBERS_API = `http://localhost:8080/api/v1/teams/${id}/members`;
  const PROJECT_DETAIL_API = (projectId) => `http://localhost:8080/api/v1/projects/${projectId}`;

  // --- GIỮ NGUYÊN CÁC HÀM FETCH CỦA EM VÀ BỔ SUNG LOGIC CHECK ---
  const fetchMembers = async () => {
    try {
      setLoadingMembers(true);
      const res = await axios.get(TEAM_MEMBERS_API, { headers });
      const data = res.data?.result ?? res.data ?? [];
      const memberList = Array.isArray(data) ? data : [];
      setMembers(memberList);

      // 👇 SỬA DÒNG NÀY: Lấy username từ localStorage để so sánh
      const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
      const myUsername = currentUser.username; 

      // So sánh m.userId (là chữ "Thanh" từ DB) với myUsername (cũng là "Thanh")
      const me = memberList.find(m => 
        String(m.userId).toLowerCase() === String(myUsername).toLowerCase()
      );

      if (me) {
        setMyRoleInTeam(me.memberRole); 
      } else if (globalRole === 'STUDENT') {
        setError("Bạn không thuộc thành viên của nhóm này.");
      }

    } catch (e) {
      console.error(e);
      message.error("Không tải được danh sách thành viên");
      setMembers([]);
    } finally {
      setLoadingMembers(false);
    }
  };

  const fetchProjectTitle = async (projectId) => {
    try {
      if (!projectId) {
        setProjectTitle("—");
        return;
      }
      setLoadingProject(true);
      const res = await axios.get(PROJECT_DETAIL_API(projectId), { headers });
      const p = res.data?.result ?? res.data;
      setProjectTitle(p?.title || "—");
    } catch (e) {
      console.error(e);
      setProjectTitle("—");
    } finally {
      setLoadingProject(false);
    }
  };

  const fetchDetail = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await axios.get(TEAM_API, { headers });
      const t = res.data?.result ?? res.data;
      setTeam(t);

      await fetchProjectTitle(t?.projectId);

      // Logic map ClassId -> Name (Giữ nguyên của em)
      try {
        const cRes = await axios.get(META_CLASSES_API, { headers });
        const raw = cRes.data?.result ?? cRes.data ?? [];
        const classes = Array.isArray(raw) ? raw : [];
        const c = classes.find((x) => String(x.id) === String(t.classId));
        setClassLabel(c?.classCode ?? c?.code ?? "—");
      } catch (e) { setClassLabel("—"); }

      // Logic check LeaderLabel (Giữ nguyên của em)
      try {
        if (t?.classId) {
          const stuRes = await axios.get(`http://localhost:8080/api/v1/teams/meta/classes/${t.classId}/students`, { headers });
          const rawStu = stuRes.data?.result ?? stuRes.data ?? [];
          const students = Array.isArray(rawStu) ? rawStu : [];
          const found = students.find((s) => String(s.studentId) === String(t.leaderId));
          setLeaderLabel(found ? found.studentId : t.leaderId || "—");
        } else { setLeaderLabel(t?.leaderId || "—"); }
      } catch (e) { setLeaderLabel(t?.leaderId || "—"); }

      await fetchMembers();
    } catch (e) {
      console.error(e);
      setError(e?.response?.data?.message || "Không thể tải chi tiết team");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDetail();
  }, [id]);

  // --- PHẦN HIỂN THỊ JSX (Giữ nguyên Style của em) ---
  if (loading) return <div style={{ padding: 40, textAlign: "center" }}><Spin size="large" /></div>;

  if (error) return <Alert type="error" message="Lỗi" description={error} showIcon action={<Button onClick={() => navigate("/teams")}>Quay lại</Button>} />;

  const memberColumns = [
    { title: "UserId", dataIndex: "userId", key: "userId", width: 180 },
    { title: "Họ tên", dataIndex: "fullName", key: "fullName", render: (v, r) => v || r?.userId || "—" },
    { 
      title: "Vai trò", 
      dataIndex: "memberRole", 
      key: "memberRole", 
      width: 140,
      render: (v) => v === "LEADER" ? <Tag color="gold" icon={<StarFilled />}>LEADER</Tag> : <Tag>MEMBER</Tag>
    },
    { title: "% đóng góp", dataIndex: "contributionPercent", key: "contributionPercent", width: 140, render: (v) => v || 0 },
  ];

  return (
    <Card style={{ borderRadius: 16 }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 16 }}>
        <div>
          <Title level={3} style={{ margin: 0 }}>{team?.name || "—"}</Title>
          {/* 👇 HIỂN THỊ VAI TRÒ CỤ THỂ TRONG NHÓM NÀY 👇 */}
          <Space style={{marginTop: 8}}>
            <Text type="secondary">Vai trò của bạn:</Text>
            {myRoleInTeam === 'LEADER' ? <Tag color="gold">Trưởng nhóm (Leader)</Tag> : <Tag color="blue">Thành viên (Member)</Tag>}
          </Space>
        </div>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate("/teams")}>Quay lại</Button>
      </div>

      <div style={{ marginTop: 16 }}>
        <Descriptions bordered column={1} size="middle">
          <Descriptions.Item label="Lớp">{classLabel}</Descriptions.Item>
          <Descriptions.Item label="Dự án">{team.projectId ? (loadingProject ? "Đang tải..." : projectTitle) : "—"}</Descriptions.Item>
          <Descriptions.Item label="Trưởng nhóm">{leaderLabel}</Descriptions.Item>
          <Descriptions.Item label="Ngày tạo">{formatISO(team?.createdAt)}</Descriptions.Item>
        </Descriptions>
      </div>

      <div style={{ marginTop: 24 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
            <Title level={5} style={{ margin: 0 }}>Thành viên nhóm</Title>
            {/* ✅ NÚT NÀY CHỈ HIỆN NẾU LÀ LEADER TRONG NHÓM NÀY */}
            {myRoleInTeam === 'LEADER' && (
                <Button type="primary" size="small" onClick={() => navigate(`/workspace/${id}`)}>
                    Quản lý Sprint/Task
                </Button>
            )}
        </div>
        <Table rowKey="userId" columns={memberColumns} dataSource={members} loading={loadingMembers} pagination={false} />
      </div>
    </Card>
  );
}