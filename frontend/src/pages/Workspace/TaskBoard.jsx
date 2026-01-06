import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Card, Col, Row, Button, Input, Modal, message, Tag, Select, Empty } from 'antd';
import { PlusOutlined, ArrowRightOutlined, ArrowLeftOutlined, DeleteOutlined } from '@ant-design/icons';

const TaskBoard = () => {
  const [tasks, setTasks] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [selectedSprintId, setSelectedSprintId] = useState(null);
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [isSprintModalOpen, setIsSprintModalOpen] = useState(false);
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newSprintName, setNewSprintName] = useState('');

  const API_BASE = 'http://localhost:8080/api/workspace';

  // --- CÁC HÀM GET STATUS ---
  const getNextStatus = (s) => s === 'BACKLOG' ? 'TODO' : s === 'TODO' ? 'IN_PROGRESS' : 'DONE';
  const getPrevStatus = (s) => s === 'DONE' ? 'IN_PROGRESS' : s === 'IN_PROGRESS' ? 'TODO' : 'BACKLOG';

  const fetchSprints = async () => {
    try {
        const res = await axios.get(`${API_BASE}/sprints`);
        const data = res.data.data || [];
        setSprints(data);
        if (data.length > 0 && !selectedSprintId) setSelectedSprintId(data[0].id);
    } catch (e) {}
  };

  const fetchTasks = async () => {
    try {
        const res = await axios.get(`${API_BASE}/tasks`); 
        setTasks(res.data.data || []);
    } catch (e) {}
  };

  useEffect(() => { fetchSprints(); fetchTasks(); }, []);

  const updateTask = async (task, newStatus, assignToCurrentSprint = false) => {
    try {
        let url = `${API_BASE}/tasks/${task.id}/status?status=${newStatus}`;
        if (assignToCurrentSprint && selectedSprintId) {
            url += `&sprintId=${selectedSprintId}`;
        }
        await axios.put(url);
        fetchTasks();
    } catch (e) { message.error("Lỗi cập nhật"); }
  };

  const handleCreateTask = async () => {
    if (!newTaskTitle) return;
    try {
      await axios.post(`${API_BASE}/tasks`, {
        title: newTaskTitle, description: "New Task", status: "BACKLOG", sprint: null 
      });
      message.success("Đã thêm vào Backlog!");
      setIsTaskModalOpen(false); setNewTaskTitle(''); fetchTasks();
    } catch (e) { message.error("Lỗi tạo task"); }
  };

  const handleCreateSprint = async () => {
    if(!newSprintName) return;
    try {
        await axios.post(`${API_BASE}/sprints`, { name: newSprintName });
        message.success("Tạo Sprint thành công!");
        setIsSprintModalOpen(false); setNewSprintName(''); fetchSprints();
    } catch (e) { message.error("Lỗi tạo Sprint"); }
  };

  const handleDeleteTask = async (id) => {
    try {
        await axios.delete(`${API_BASE}/tasks/${id}`);
        message.success("Đã xóa task");
        fetchTasks();
    } catch(e) { message.error("Lỗi xóa task"); }
  };

  const handleDeleteSprint = async () => {
    if(!selectedSprintId) return; 
    
    try {
        await axios.delete(`${API_BASE}/sprints/${selectedSprintId}`);
        message.success("Đã xóa Sprint");
        setSelectedSprintId(null);
        fetchSprints();
    } catch(e) { message.error("Lỗi xóa Sprint"); }
  };

  const renderColumn = (title, status, color, isBacklog = false) => {
    const filteredTasks = tasks.filter(t => {
        if (status === 'BACKLOG') return t.status === 'BACKLOG';
        return t.status === status && t.sprint?.id == selectedSprintId; 
    });

    return (
      <Col span={6}>
        <Card 
            title={<Tag color={color} style={{width:'100%', textAlign:'center'}}>{title} ({filteredTasks.length})</Tag>} 
            style={{ backgroundColor: isBacklog ? '#fff1f0' : '#f0f2f5', border: 'none' }}
            bodyStyle={{ padding: '10px', height: '600px', overflowY: 'auto' }}
        >
          {filteredTasks.length === 0 && <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="Trống" />}
          
          {filteredTasks.map(task => (
            <Card key={task.id} size="small" style={{ marginBottom: '10px' }} hoverable>
              <div style={{display: 'flex', justifyContent: 'space-between', alignItems:'flex-start'}}>
                  <b style={{wordBreak:'break-word'}}>{task.title}</b>
                  <Button type="text" danger size="small" icon={<DeleteOutlined />} onClick={() => handleDeleteTask(task.id)} />
              </div>
              
              <div style={{ marginTop: '10px', display: 'flex', justifyContent: 'space-between' }}>
                 {status !== 'BACKLOG' && <Button size="small" icon={<ArrowLeftOutlined />} onClick={() => updateTask(task, getPrevStatus(status))} />}
                 {status !== 'DONE' && <Button type="primary" size="small" icon={<ArrowRightOutlined />} onClick={() => updateTask(task, getNextStatus(status), status === 'BACKLOG')} />}
              </div>
            </Card>
          ))}
        </Card>
      </Col>
    );
  };

  return (
    <div style={{ padding: '20px' }}>
      <Card style={{ marginBottom: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                <span style={{ fontSize: '16px', fontWeight: 'bold' }}>🚀 Sprint Board:</span>
                <Select value={selectedSprintId} style={{ width: 200 }} onChange={setSelectedSprintId} placeholder="Chọn Sprint...">
                    {sprints.map(s => <Select.Option key={s.id} value={s.id}>{s.name}</Select.Option>)}
                </Select>
                <Button icon={<PlusOutlined />} onClick={() => setIsSprintModalOpen(true)}>Tạo Sprint</Button>
                {selectedSprintId && <Button danger icon={<DeleteOutlined />} onClick={handleDeleteSprint} />}
            </div>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setIsTaskModalOpen(true)}>Thêm Task vào Backlog</Button>
        </div>
      </Card>

      <Row gutter={16}>
        {renderColumn('Kho (Backlog)', 'BACKLOG', 'default', true)}
        {renderColumn('Cần làm (Todo)', 'TODO', 'blue')}
        {renderColumn('Đang làm', 'IN_PROGRESS', 'orange')}
        {renderColumn('Hoàn thành', 'DONE', 'green')}
      </Row>

      <Modal title="Thêm công việc" open={isTaskModalOpen} onOk={handleCreateTask} onCancel={() => setIsTaskModalOpen(false)}>
        <Input placeholder="Tên công việc..." value={newTaskTitle} onChange={e => setNewTaskTitle(e.target.value)} onPressEnter={handleCreateTask}/>
      </Modal>
      <Modal title="Tạo Sprint" open={isSprintModalOpen} onOk={handleCreateSprint} onCancel={() => setIsSprintModalOpen(false)}>
        <Input placeholder="Tên Sprint..." value={newSprintName} onChange={e => setNewSprintName(e.target.value)} onPressEnter={handleCreateSprint} />
      </Modal>
    </div>
  );
};

export default TaskBoard;