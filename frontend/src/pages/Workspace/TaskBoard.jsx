import React, { useState, useEffect, useCallback } from 'react';
import { Card, Col, Row, Button, Input, Modal, message, Tag, Select, Tooltip, Avatar, Typography, Space, List } from 'antd';
import { 
  PlusOutlined, ArrowRightOutlined, ArrowLeftOutlined, 
  DeleteOutlined, UserOutlined, BankOutlined, TeamOutlined 
} from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const TaskBoard = () => {
  // Lấy cả workspaceId và classId từ URL (nếu có)
  // Route Nhóm: /workspace/:id
  // Route Lớp:  /workspace/:id/class/:classId/board
  const { id: workspaceId, classId } = useParams(); 
  const navigate = useNavigate();

  // --- BIẾN CỜ XÁC ĐỊNH CHẾ ĐỘ ---
  const isClassMode = !!classId; // True nếu đang ở giao diện Lớp

  const [tasks, setTasks] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [selectedSprintId, setSelectedSprintId] = useState(null);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(false);
  
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [isSprintModalOpen, setIsSprintModalOpen] = useState(false);
  const [isAssignModalOpen, setIsAssignModalOpen] = useState(false);
  
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newSprintName, setNewSprintName] = useState('');
  const [selectedTask, setSelectedTask] = useState(null);
  
  // State lưu ID của Team (chỉ dùng khi ở chế độ Team)
  const [currentTeamId, setCurrentTeamId] = useState(null);

  const API_BASE = 'http://localhost:8080/api/workspace';
  const API_CLASS = 'http://localhost:8080/api/classes';
  const token = localStorage.getItem('token');
  const config = { headers: { Authorization: `Bearer ${token}` } };
  
  // --- 1. TẢI DỮ LIỆU ---
  // 1. Tải dữ liệu
  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      let teamIdToUse = null;

      // A. XỬ LÝ ID (Lấy Team ID nếu đang ở chế độ Nhóm)
      if (!isClassMode) {
          const wsRes = await axios.get(`${API_BASE}/workspaces/${workspaceId}`, config);
          teamIdToUse = wsRes.data.result?.teamId;
          
          if (teamIdToUse) {
              setCurrentTeamId(teamIdToUse);
          }
      }

      // B. LẤY SPRINTS (ĐÃ SỬA: Tách biệt Sprint Lớp & Sprint Team)
      let sprintUrl = `${API_BASE}/sprints/by-workspace/${workspaceId}`;

      // Logic ghép tham số:
      if (isClassMode) {
          sprintUrl += `?classId=${classId}`;
      } else if (teamIdToUse) {
          sprintUrl += `?teamId=${teamIdToUse}`;
      }

      const sprRes = await axios.get(sprintUrl, config);
      setSprints(sprRes.data.result || []);

      if (sprRes.data.result?.length > 0) {
          if (!selectedSprintId) {
             setSelectedSprintId(sprRes.data.result[0].id);
          }
      } else {
          setSelectedSprintId(null);
      }
      // -----------------------------------------------------------


      // C. Lấy Members (Logic rẽ nhánh như cũ)
      if (isClassMode) {
          const memRes = await axios.get(`${API_CLASS}/${classId}/workspace-members`, config);
          setMembers(memRes.data.result || memRes.data || []);
      } else if (teamIdToUse) {
          const memRes = await axios.get(`http://localhost:8080/api/v1/teams/${teamIdToUse}/members`, config);
          setMembers(memRes.data.result || memRes.data || []);
      }

      // D. Lấy Tasks (Cũng phải lọc task theo ngữ cảnh)
      let taskUrl = `${API_BASE}/tasks?workspaceId=${workspaceId}`;
      
      if (isClassMode) {
          taskUrl += `&classId=${classId}`; // Lọc theo Lớp
      } else if (teamIdToUse) {
          taskUrl += `&teamId=${teamIdToUse}`; // Lọc theo Team
      }
      
      const taskRes = await axios.get(taskUrl, config);
      setTasks(taskRes.data.result || []);

    } catch (e) { 
        console.error("Lỗi tải dữ liệu:", e); 
    } finally { 
        setLoading(false); 
    }
  }, [workspaceId, classId, selectedSprintId, isClassMode]);

  useEffect(() => { fetchData(); }, [fetchData]);

  // --- 2. XỬ LÝ SPRINT ---
  const handleCreateSprint = async () => {
    try {
        const payload = { 
            name: newSprintName, 
            workspace: { id: workspaceId } 
        };

        // --- PHÂN LOẠI RẠCH RÒI KHI TẠO ---
        if (isClassMode && classId) {
            // Tạo cho LỚP
            payload.classId = classId;
            payload.teamId = null; 
        } else if (!isClassMode && currentTeamId) {
            // Tạo cho NHÓM
            payload.teamId = currentTeamId;
            payload.classId = null; 
        } else {
            message.error("Không xác định được ngữ cảnh (Lớp/Team) để tạo Sprint");
            return;
        }

        await axios.post(`${API_BASE}/sprints`, payload, config);
        message.success("Đã tạo Sprint thành công");
        setIsSprintModalOpen(false); 
        setNewSprintName(''); 
        fetchData();
    } catch (e) { 
        console.error(e);
        message.error("Lỗi tạo Sprint"); 
    }
  };

  const handleDeleteSprint = async () => {
    if (!selectedSprintId) return;
    if (!window.confirm("Xóa Sprint này? Task sẽ về Backlog.")) return;
    try {
        await axios.delete(`${API_BASE}/sprints/${selectedSprintId}`, config);
        message.success("Đã xóa Sprint");
        setSelectedSprintId(null); fetchData();
    } catch (e) { message.error("Lỗi khi xóa"); }
  };

  // --- 3. XỬ LÝ TASK ---
  const handleCreateTask = async () => {
    if (!isClassMode && !currentTeamId) {
        message.error("Lỗi: Không tìm thấy Team ID"); return;
    }

    try {
      let createUrl = `${API_BASE}/tasks?workspaceId=${workspaceId}`;
      
      if (isClassMode) {
          createUrl += `&classId=${classId}`;
      } else {
          createUrl += `&teamId=${currentTeamId}`;
      }

      await axios.post(createUrl, { title: newTaskTitle, status: "BACKLOG" }, config);

      message.success(isClassMode ? "Đã giao bài tập cho lớp" : "Đã thêm task cho nhóm");
      setIsTaskModalOpen(false); 
      setNewTaskTitle(''); 
      fetchData();
    } catch (e) { message.error("Lỗi tạo task"); }
  };

  const updateTaskStatus = async (task, newStatus) => {
    if (newStatus === 'TODO' && !selectedSprintId) {
        message.warning("Hãy chọn một Sprint trước!"); return;
    }
    try {
        let url = `${API_BASE}/tasks/${task.id}/status?status=${newStatus}`;
        if (selectedSprintId) url += `&sprintId=${selectedSprintId}`;
        await axios.put(url, {}, config); fetchData();
    } catch (e) { message.error("Lỗi cập nhật"); }
  };

const handleAssignUser = async (userId) => {
    try {
        await axios.put(`${API_BASE}/tasks/${selectedTask.id}/assign?assigneeId=${userId}`, {}, config);
        message.success("Đã giao việc thành công!");
        setIsAssignModalOpen(false);
        fetchData();
    } catch (e) { 
        message.error("Lỗi giao việc"); 
    }
};

  // --- RENDER ---
  const renderColumn = (title, status, color, isBacklog = false) => {
    const filteredTasks = tasks.filter(t => isBacklog ? t.status === 'BACKLOG' : (t.status === status && t.sprint?.id == selectedSprintId));

    return (
      <Col span={6}>
        <Card title={<Tag color={color} style={{width:'100%', textAlign:'center'}}>{title} ({filteredTasks.length})</Tag>} 
              style={{ backgroundColor: isBacklog ? '#fff1f0' : '#f0f2f5', borderRadius: '12px' }}
              bodyStyle={{ padding: '10px', height: '550px', overflowY: 'auto' }}>
          {filteredTasks.map(task => (
            <Card key={task.id} size="small" style={{ marginBottom: '10px' }} hoverable>
              <div style={{display:'flex', justifyContent: 'space-between'}}>
                  <b style={{fontSize: '13px'}}>{task.title}</b>
                  <Button type="text" danger size="small" icon={<DeleteOutlined />} onClick={() => {
                      if(window.confirm("Xóa task?")) axios.delete(`${API_BASE}/tasks/${task.id}`, config).then(()=>fetchData());
                  }} />
              </div>

              <div style={{marginTop:'10px'}}>
                 {task.assigneeId ? (
                    <Tooltip title={`Người làm: ${task.assigneeId}`}>
                        <Avatar style={{backgroundColor: '#87d068'}} icon={<UserOutlined />} size="small"/>
                        <span style={{marginLeft: 5, fontSize: 12}}>{task.assigneeId}</span>
                    </Tooltip>
                 ) : (
                    <Button size="small" type="dashed" shape="circle" icon={<PlusOutlined style={{fontSize: '10px'}}/>} 
                            onClick={() => { setSelectedTask(task); setIsAssignModalOpen(true); }} 
                    />
                 )}
              </div>
              
              <div style={{ marginTop: '12px', display: 'flex', justifyContent: 'space-between' }}>
                 {status !== 'BACKLOG' && <Button size="small" icon={<ArrowLeftOutlined />} onClick={() => updateTaskStatus(task, status === 'TODO' ? 'BACKLOG' : status === 'IN_PROGRESS' ? 'TODO' : 'IN_PROGRESS')} />}
                 {status !== 'DONE' && <Button type="primary" size="small" icon={<ArrowRightOutlined />} onClick={() => updateTaskStatus(task, status === 'BACKLOG' ? 'TODO' : status === 'TODO' ? 'IN_PROGRESS' : 'DONE', status === 'BACKLOG')} />}
              </div>
            </Card>
          ))}
        </Card>
      </Col>
    );
  };

  return (
    <div style={{ padding: '0px' }}>
      <div style={{ marginBottom: '20px', display: 'flex', justifyContent: 'space-between' }}>
        <Space size="large">
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>Quay lại</Button>
            
            {/* HIỂN THỊ LABEL ĐỂ BIẾT ĐANG Ở ĐÂU */}
            {isClassMode ? (
                 <Tag icon={<BankOutlined />} color="purple" style={{padding: '5px 10px', fontSize: '14px'}}>LỚP HỌC</Tag>
            ) : (
                 <Tag icon={<TeamOutlined />} color="blue" style={{padding: '5px 10px', fontSize: '14px'}}>NHÓM DỰ ÁN</Tag>
            )}

            <div style={{ background: '#fff', padding: '5px 15px', borderRadius: '20px', border: '1px solid #d9d9d9' }}>
                <span style={{ fontWeight: 'bold' }}>Sprint: </span>
                <Select value={selectedSprintId} style={{ width: 150 }} onChange={setSelectedSprintId} variant="borderless" placeholder="Chọn Sprint">
                    {sprints.map(s => <Select.Option key={s.id} value={s.id}>{s.name}</Select.Option>)}
                </Select>
                {selectedSprintId && <Button type="text" danger icon={<DeleteOutlined />} onClick={handleDeleteSprint} />}
            </div>
            <Button icon={<PlusOutlined />} onClick={() => setIsSprintModalOpen(true)}>Thêm Sprint</Button>
        </Space>
        <Button type="primary" size="large" icon={<PlusOutlined />} onClick={() => setIsTaskModalOpen(true)}>
            {isClassMode ? 'Giao bài tập mới' : 'Thêm Task mới'}
        </Button>
      </div>

      <Row gutter={16}>
        {renderColumn('📌 Kho (Backlog)', 'BACKLOG', 'default', true)}
        {renderColumn('📋 Cần làm (Todo)', 'TODO', 'blue')}
        {renderColumn('🔥 Đang làm', 'IN_PROGRESS', 'orange')}
        {renderColumn('✅ Hoàn thành', 'DONE', 'green')}
      </Row>

      {/* Modal Gán Người làm - Có hiển thị Role nếu là lớp */}
      <Modal title="Phân công công việc" open={isAssignModalOpen} footer={null} onCancel={() => setIsAssignModalOpen(false)}>
        <List
            dataSource={members}
            renderItem={m => (
                <List.Item actions={[<Button type="link" onClick={() => handleAssignUser(m.userId)}>Giao việc</Button>]}>
                    <List.Item.Meta 
                        avatar={<Avatar src={`https://api.dicebear.com/7.x/avataaars/svg?seed=${m.userId}`} />} 
                        title={
                            <Space>
                                {m.fullName || m.userId}
                                {/* Chỉ hiện Role nếu có (ClassMode) */}
                                {m.role && <Tag color={m.role === 'TEACHER' ? 'red' : 'blue'}>{m.role}</Tag>}
                            </Space>
                        } 
                    />
                </List.Item>
            )}
        />
      </Modal>

      <Modal title={isClassMode ? "Giao bài tập" : "Thêm công việc"} open={isTaskModalOpen} onOk={handleCreateTask} onCancel={() => setIsTaskModalOpen(false)}>
        <Input placeholder="Tên công việc..." value={newTaskTitle} onChange={e => setNewTaskTitle(e.target.value)} />
      </Modal>

      <Modal title="Tạo Sprint" open={isSprintModalOpen} onOk={handleCreateSprint} onCancel={() => setIsSprintModalOpen(false)}>
        <Input placeholder="Tên Sprint..." value={newSprintName} onChange={e => setNewSprintName(e.target.value)} />
      </Modal>
    </div>
  );
};

export default TaskBoard;