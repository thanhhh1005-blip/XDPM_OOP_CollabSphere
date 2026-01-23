import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { Card, Form, Input, Button, Select, message, Space, Avatar } from "antd";
import { getAuthInfo } from "../../utils/authStorage";

export default function TeamEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form] = Form.useForm();

  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [initialMemberIds, setInitialMemberIds] = useState([]);

  // --- THEO DÕI GIÁ TRỊ FORM ---
  const watchedLeaderId = Form.useWatch('leaderId', form);
  const watchedMemberIds = Form.useWatch('memberIds', form) || [];
  console.log("Watched Member IDs:", watchedMemberIds);

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
        // 1. Load thông tin team
        const teamRes = await axios.get(`http://localhost:8080/api/v1/teams/${id}`, { headers });
        const team = teamRes.data?.result ?? teamRes.data;

        // 2. Load DANH SÁCH THÀNH VIÊN
        const membersRes = await axios.get(`http://localhost:8080/api/v1/teams/${id}/members`, { headers });
        const rawMembers = membersRes.data?.result ?? membersRes.data ?? [];
        
        // 🚀 ĐOẠN FIX QUAN TRỌNG: Dò tìm ID (thử cả uid và userId)
        const currentIds = rawMembers
          .map(m => {
            // Thử lấy uid, nếu không có thì lấy userId, nếu không có nữa thì lấy username
            const idValue = m.uid || m.userId || m.username || m.studentId;
            return idValue ? String(idValue) : null;
          })
          .filter(id => id !== null); // Loại bỏ những cái null/undefined thực sự

        console.log("Dữ liệu thành viên đã lọc sạch ID:", currentIds);

        // 3. Load toàn bộ sinh viên trong lớp
        const stuRes = await axios.get(`http://localhost:8080/api/v1/teams/meta/classes/${team.classId}/students`, { headers });
        const allStu = stuRes.data?.result ?? stuRes.data;
        setStudents(allStu);

        // 4. Cập nhật State và Form
        setInitialMemberIds(currentIds);
        form.setFieldsValue({
          name: team.name,
          leaderId: String(team.leaderId),
          memberIds: currentIds,
        });

      } catch (e) {
        console.error("Lỗi chi tiết:", e);
        message.error("Không tải được dữ liệu team");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id, form]);

  // --- LOGIC LỌC SIÊU SẠCH (CHỐT HẠ) ---
  const filteredOptions = useMemo(() => {
    // Tập hợp tất cả những người "đã được chọn" trên giao diện
    const pickedIds = new Set([
      String(watchedLeaderId || ""),
      ...watchedMemberIds.map(String)
    ]);

    return students
      .filter((s) => {
        const sId = String(s.studentId);
        
        // Nếu sId nằm trong nhóm đã chọn -> ẨN LUÔN (trả về false)
        if (pickedIds.has(sId)) return false;

        // Nếu sinh viên thuộc team khác (hasTeam = true) nhưng KHÔNG phải team mình đang sửa
        // thì cũng ẩn luôn để tránh chọn trùng người của team khác
        if (s.hasTeam && !initialMemberIds.includes(sId)) return false;

        return true;
      })
      .map((s) => ({
        value: s.studentId,
        label: `${s.fullName || s.studentId} (${s.studentId})`,
      }));
  }, [students, watchedLeaderId, watchedMemberIds, initialMemberIds]);

  // --- HÀM LƯU DỮ LIỆU ---
  const onFinish = async (values) => {
    try {
      const params = new URLSearchParams();
      params.append("name", values.name);
      
      const lid = String(values.leaderId);
      if (lid) params.append("leaderId", lid);

      // CỰC KỲ QUAN TRỌNG: Lọc bỏ Leader ra khỏi MemberIds để tránh lỗi 500 Duplicate
      const finalMemberIds = (values.memberIds || [])
        .map(String)
        .filter(mId => mId !== lid);

      finalMemberIds.forEach((m) => params.append("memberIds", m));

      await axios.put(`http://localhost:8080/api/v1/teams/${id}`, null, {
        headers,
        params,
      });

      message.success("Cập nhật team thành công");
      navigate(`/teams/${id}`);
    } catch (e) {
      console.error(e);
      message.error(e?.response?.data?.message || "Lỗi cập nhật hệ thống (500)");
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
            placeholder="Chọn trưởng nhóm"
            showSearch
            optionFilterProp="label"
            options={filteredOptions} 
            // Nếu muốn đổi trưởng nhóm, list này chỉ hiện những người "chưa chọn"
          />
        </Form.Item>

        <Form.Item name="memberIds" label="Thành viên nhóm">
          <Select
            mode="multiple"
            placeholder="Chọn thành viên (Người đã chọn sẽ biến mất hoàn toàn ở đây)"
            showSearch
            optionFilterProp="label"
            options={filteredOptions} // Dùng chung list đã lọc sạch
          />
        </Form.Item>

        <Space style={{ marginTop: 20 }}>
          <Button onClick={() => navigate("/teams")}>Hủy</Button>
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