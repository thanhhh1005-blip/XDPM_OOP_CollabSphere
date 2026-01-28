import React, { useState, useEffect } from 'react';
// 👇 Đảm bảo đường dẫn import đúng với project của bạn
import ScheduleCalendar from '../../components/ScheduleCalendar'; 
import axios from 'axios';
import { Modal, Button, Form, Input, DatePicker, Select, notification, Popconfirm } from 'antd';
import moment from 'moment';

const ClassSchedule = () => {
  // --- 1. STATE & CONTEXT ---
  const savedUser = JSON.parse(localStorage.getItem('user') || '{}');
  const userRole = savedUser.role || "STAFF"; 

  const [classList, setClassList] = useState([]); 
  const [selectedClassId, setSelectedClassId] = useState(savedUser.classId || null);
  
  // ✅ BIẾN QUAN TRỌNG: Danh sách Teams lấy từ API
  const [teams, setTeams] = useState([]); 

  // State Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedEventId, setSelectedEventId] = useState(null);
  const [reloadKey, setReloadKey] = useState(0); 
  const [form] = Form.useForm();

  // --- 2. API: LOAD DANH SÁCH LỚP ---
  useEffect(() => {
    const fetchClasses = async () => {
      if (userRole !== 'STAFF' && userRole !== 'ADMIN') return;
      try {
        const res = await axios.get('http://localhost:8080/api/classes');
        const data = res.data.result || res.data || [];
        setClassList(data);
        if (!selectedClassId && data.length > 0) setSelectedClassId(data[0].id);
      } catch (e) { console.error("Lỗi load lớp:", e); }
    };
    fetchClasses();
  }, []);

  // --- 3. API: LOAD TEAM (Chạy khi đổi lớp) ---
  useEffect(() => {
    const fetchTeams = async () => {
      if (!selectedClassId) return;
      try {
        const res = await axios.get(`http://localhost:8080/api/v1/teams/class/${selectedClassId}`);
        // Lưu danh sách team vào state để lát truyền xuống Lịch
        setTeams(res.data.result || res.data || []);
      } catch (e) { 
        console.error("Lỗi load team:", e);
        setTeams([]); 
      }
    };
    fetchTeams();
  }, [selectedClassId]);

  // --- 4. CÁC HÀM XỬ LÝ (Mở Modal, Lưu, Xóa) ---
  const handleOpenCreate = () => {
    setIsEditMode(false);
    form.resetFields();
    form.setFieldsValue({
      type: 'TEAM',
      startTime: moment().startOf('hour').add(1, 'hour'),
      endTime: moment().startOf('hour').add(2, 'hour')
    });
    setIsModalOpen(true);
  };

  const handleEventClick = (info) => {
    if (userRole !== 'STAFF' && userRole !== 'ADMIN') return;
    const event = info.event;
    const props = event.extendedProps;
    setIsEditMode(true);
    setSelectedEventId(event.id);
    
    // Parse lại ngày giờ để hiện lên Form
    form.setFieldsValue({
      // Lấy title gốc (bỏ phần tên team đã ghép nếu có)
      title: props.rawTitle || event.title.split(' - ').pop(), 
      type: props.type,
      teamId: props.teamId,
      location: props.location,
      startTime: moment(event.start),
      endTime: moment(event.end),
    });
    setIsModalOpen(true);
  };

  const handleSave = async (values) => {
    try {
      if (!selectedClassId) {
          notification.error({message: "Chưa chọn lớp học!"});
          return;
      }
      
      const payload = {
        ...values,
        classId: selectedClassId,
        // Format ngày chuẩn để Backend Java không lỗi
        startTime: values.startTime.format("YYYY-MM-DDTHH:mm:ss"),
        endTime: values.endTime.format("YYYY-MM-DDTHH:mm:ss")
      };

      if (isEditMode) {
        await axios.put(`http://localhost:8080/api/schedules/${selectedEventId}`, payload);
        notification.success({ message: 'Cập nhật thành công!' });
      } else {
        await axios.post('http://localhost:8080/api/schedules', payload);
        notification.success({ message: 'Tạo mới thành công!' });
      }
      setIsModalOpen(false);
      setReloadKey(prev => prev + 1); // Reload lại lịch
    } catch (error) {
      notification.error({ message: 'Lỗi', description: error.response?.data?.message || "Lỗi server" });
    }
  };

  const handleDelete = async () => {
    try {
      await axios.delete(`http://localhost:8080/api/schedules/${selectedEventId}`);
      notification.success({ message: 'Đã xóa!' });
      setIsModalOpen(false);
      setReloadKey(prev => prev + 1);
    } catch (error) {
      notification.error({ message: 'Lỗi xóa', description: error.message });
    }
  };

  return (
    <div className="flex flex-col h-full bg-gray-50 min-h-screen p-6">
      {/* HEADER */}
      <div className="flex justify-between items-start mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">📅 Quản lý Lịch Review</h1>
          {(userRole === 'STAFF' || userRole === 'ADMIN') ? (
            <div className="mt-2 flex items-center gap-2">
                <span className="text-gray-600">Lớp:</span>
                <Select
                    showSearch
                    style={{ width: 250 }}
                    placeholder="Chọn lớp..."
                    optionFilterProp="children"
                    onChange={setSelectedClassId}
                    value={selectedClassId}
                    options={classList.map(cls => ({
                        value: cls.id,
                        label: `${cls.classCode || cls.code} - ${cls.className || cls.name}`
                    }))}
                />
            </div>
          ) : (
            <p className="text-gray-500">Lớp của tôi</p>
          )}
        </div>
        {(userRole === 'STAFF' || userRole === 'ADMIN') && (
          <Button type="primary" size="large" onClick={handleOpenCreate} disabled={!selectedClassId}>
            + Tạo Lịch
          </Button>
        )}
      </div>

      {/* COMPONENT LỊCH */}
      <div className="bg-white rounded-xl shadow p-4 flex-1">
         {selectedClassId ? (
             <ScheduleCalendar 
                classId={selectedClassId} 
                // 👇 QUAN TRỌNG: TRUYỀN BIẾN TEAMS XUỐNG ĐỂ CON TỰ GHÉP TÊN
                key={`${selectedClassId}-${reloadKey}`} 
                onEventClick={handleEventClick} 
             />
         ) : (
             <div className="text-center p-10 text-gray-400">Vui lòng chọn lớp để xem lịch</div>
         )}
      </div>

      {/* MODAL FORM */}
      <Modal
        title={isEditMode ? "Sửa lịch" : "Tạo lịch mới"}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        footer={null}
        destroyOnClose={true}
      >
        <Form form={form} layout="vertical" onFinish={handleSave}>
          <Form.Item name="title" label="Tiêu đề (VD: Review Sprint 1)" rules={[{ required: true }]}>
            <Input />
          </Form.Item>

          <div className="grid grid-cols-2 gap-4">
            <Form.Item name="startTime" label="Bắt đầu" rules={[{ required: true }]}>
              <DatePicker showTime format="YYYY-MM-DD HH:mm" />
            </Form.Item>
            <Form.Item name="endTime" label="Kết thúc" rules={[{ required: true }]}>
              <DatePicker showTime format="YYYY-MM-DD HH:mm" />
            </Form.Item>
          </div>

          <Form.Item name="location" label="Địa điểm / Link">
            <Input />
          </Form.Item>

          <div className="grid grid-cols-2 gap-4">
            <Form.Item name="type" label="Đối tượng">
                <Select onChange={() => form.setFieldsValue({teamId: null})}> 
                    <Select.Option value="TEAM">Review Nhóm</Select.Option>
                    <Select.Option value="CLASS">Cả Lớp</Select.Option>
                </Select>
            </Form.Item>

            <Form.Item noStyle shouldUpdate={(prev, current) => prev.type !== current.type}>
              {({ getFieldValue }) => 
                getFieldValue('type') === 'TEAM' ? (
                  <Form.Item name="teamId" label="Chọn Nhóm" rules={[{ required: true }]}>
                    <Select showSearch optionFilterProp="children" placeholder="Tìm tên nhóm...">
                      {teams.map((team) => (
                        <Select.Option key={team.id} value={team.id}>
                          {/* Hiển thị tên nhóm ở Dropdown */}
                          {team.teamName || team.name} 
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                ) : null
              }
            </Form.Item>
          </div>

          <div className="flex justify-end gap-2 mt-4 pt-4 border-t">
            {isEditMode && (
               <Popconfirm title="Xóa lịch này?" onConfirm={handleDelete}>
                  <Button danger>Xóa</Button>
               </Popconfirm>
            )}
            <Button onClick={() => setIsModalOpen(false)}>Hủy</Button>
            <Button type="primary" htmlType="submit">Lưu</Button>
          </div>
        </Form>
      </Modal>
    </div>
  );
};

export default ClassSchedule;