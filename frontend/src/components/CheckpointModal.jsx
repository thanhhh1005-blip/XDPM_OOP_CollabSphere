import React, { useState, useEffect } from 'react';
import { Modal, List, Checkbox, Button, Input, Select, Progress, Tag, message, Popconfirm, Empty, InputNumber } from 'antd';
import { PlusOutlined, DeleteOutlined, CheckSquareOutlined, UserOutlined, EditOutlined, StarOutlined, MessageOutlined } from '@ant-design/icons';
import axios from 'axios';

const CheckpointModal = ({ 
    isOpen, onClose, milestone, teamId, teamMembers, 
    isLeader, currentUser, isLecturer // Nhận thêm props isLecturer
}) => {
    const [subTasks, setSubTasks] = useState([]);
    const [newTitle, setNewTitle] = useState("");
    const [assignedUser, setAssignedUser] = useState(null);
    const [loading, setLoading] = useState(false);
    
    // State cho chấm điểm
    const [editingTask, setEditingTask] = useState(null);
    const [gradeInput, setGradeInput] = useState({ score: 0, comment: '' });
    
    const token = localStorage.getItem('token');

    // Load dữ liệu
    useEffect(() => {
        if (isOpen && milestone && teamId) {
            fetchSubTasks();
        }
    }, [isOpen, milestone, teamId]);

    const fetchSubTasks = async () => {
        try {
            const res = await axios.get(`http://localhost:8080/api/workspace/subtasks`, {
                params: { milestoneId: milestone.id, teamId: teamId },
                headers: { Authorization: `Bearer ${token}` }
            });
            setSubTasks(res.data.result || []);
        } catch (error) {
            console.error(error);
        }
    };

    const handleAdd = async () => {
        if (!newTitle.trim()) return;
        setLoading(true);
        try {
            await axios.post(`http://localhost:8080/api/workspace/subtasks`, {
                title: newTitle,
                milestoneId: milestone.id,
                teamId: teamId,
                assignedTo: assignedUser
            }, { headers: { Authorization: `Bearer ${token}` } });
            
            message.success("Đã thêm checkpoint");
            setNewTitle("");
            setAssignedUser(null);
            fetchSubTasks();
        } catch (error) {
            message.error("Lỗi thêm mới");
        } finally {
            setLoading(false);
        }
    };

    const handleToggle = async (task) => {
        if (isLecturer) return; // GV không được tích hoàn thành thay SV
        if (!isLeader && task.assignedTo !== currentUser) {
            message.warning("Bạn không được phân công checkpoint này!");
            return;
        }
        try {
            await axios.put(`http://localhost:8080/api/workspace/subtasks/${task.id}/toggle`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            fetchSubTasks(); 
        } catch (error) {
            message.error("Lỗi cập nhật");
        }
    };

    const handleDelete = async (id) => {
        try {
            await axios.delete(`http://localhost:8080/api/workspace/subtasks/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            message.success("Đã xóa");
            fetchSubTasks();
        } catch (error) {
            message.error("Lỗi xóa");
        }
    };

    const handleSaveGrade = async () => {
        try {
            await axios.put(`http://localhost:8080/api/workspace/subtasks/${editingTask.id}/grade`, {
                score: gradeInput.score,
                comment: gradeInput.comment
            }, { headers: { Authorization: `Bearer ${token}` } });
            
            message.success("Đã chấm task!");
            setEditingTask(null);
            fetchSubTasks();
        } catch (e) {
            message.error("Lỗi lưu điểm task");
        }
    };

    const completedCount = subTasks.filter(t => t.completed).length;
    const totalCount = subTasks.length;
    const percent = (totalCount && totalCount > 0) ? Math.round((completedCount / totalCount) * 100) : 0;

    return (
        <>
            {/* --- MODAL 1: DANH SÁCH CHECKPOINT --- */}
            <Modal 
                title={<span><CheckSquareOutlined /> Checkpoints: {milestone?.title}</span>} 
                open={isOpen} 
                onCancel={onClose} 
                footer={null}
                width={750}
            >
                <div style={{ marginBottom: 20 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 5 }}>
                        <strong>Tiến độ hoàn thành:</strong>
                        <span>{completedCount}/{totalCount} checkpoints</span>
                    </div>
                    <Progress percent={percent} status={percent === 100 ? "success" : "active"} strokeColor={{ from: '#108ee9', to: '#87d068' }} />
                </div>

                {/* Form thêm task (Chỉ hiện cho Leader & không phải GV) */}
                {isLeader && !isLecturer && (
                    <div style={{ display: 'flex', gap: 10, marginBottom: 20, background: '#f0f5ff', padding: 15, borderRadius: 8, border: '1px solid #adc6ff' }}>
                        <Input 
                            placeholder="Nội dung checkpoint (VD: Thiết kế DB)" 
                            value={newTitle}
                            onChange={e => setNewTitle(e.target.value)}
                        />
                        <Select 
                            placeholder="Giao cho..." 
                            style={{ width: 180 }}
                            value={assignedUser}
                            onChange={setAssignedUser}
                            allowClear
                        >
                            {teamMembers.map(mem => (
                                <Select.Option key={mem.userId} value={mem.userId}>
                                    {mem.userId} ({mem.role || 'Member'})
                                </Select.Option>
                            ))}
                        </Select>
                        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd} loading={loading}>
                            Thêm
                        </Button>
                    </div>
                )}

                <List
                    locale={{ emptyText: <Empty description="Chưa có checkpoint nào" /> }}
                    dataSource={subTasks}
                    renderItem={item => {
                        const actions = [];
                        
                        // Action cho GIẢNG VIÊN (Chấm điểm)
                        if (isLecturer) {
                            actions.push(
                                <Button type="link" size="small" icon={<EditOutlined />} onClick={() => {
                                    setEditingTask(item);
                                    setGradeInput({ score: item.score, comment: item.comment });
                                }}>
                                    {item.score ? `${item.score}đ` : "Chấm điểm"}
                                </Button>
                            );
                        } 
                        // Action cho SINH VIÊN (Xóa)
                        else if (isLeader) {
                            actions.push(
                                <Popconfirm title="Xóa?" onConfirm={() => handleDelete(item.id)}>
                                    <Button type="text" danger icon={<DeleteOutlined />} />
                                </Popconfirm>
                            );
                        }

                        return (
                            <List.Item actions={actions} style={{ background: item.score ? '#f6ffed' : 'transparent', padding: '10px', borderRadius: '6px', marginBottom: '8px', border: item.score ? '1px solid #b7eb8f' : '1px solid #f0f0f0' }}>
                                <List.Item.Meta
                                    avatar={
                                        <Checkbox 
                                            checked={item.completed} 
                                            disabled={isLecturer || (!isLeader && item.assignedTo !== currentUser)} 
                                            onChange={() => handleToggle(item)} 
                                        />
                                    }
                                    title={
                                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                            <span style={{ textDecoration: item.completed ? 'line-through' : 'none', fontWeight: 500 }}>
                                                {item.title}
                                            </span>
                                            {/* Hiển thị điểm số nổi bật */}
                                            {item.score != null && (
                                                <Tag color="gold" icon={<StarOutlined />}>
                                                    {item.score} điểm
                                                </Tag>
                                            )}
                                        </div>
                                    }
                                    description={
                                        <div style={{ marginTop: 4 }}>
                                            <Tag icon={<UserOutlined />}>{item.assignedTo || "Chưa gán"}</Tag>
                                            {item.comment && (
                                                <div style={{ marginTop: 6, color: '#faad14', fontSize: 13, fontStyle: 'italic' }}>
                                                    <MessageOutlined /> <b>GV:</b> {item.comment}
                                                </div>
                                            )}
                                        </div>
                                    }
                                />
                            </List.Item>
                        );
                    }}
                />
            </Modal>

            {/* --- MODAL 2: FORM CHẤM ĐIỂM (Chỉ hiện khi GV bấm nút sửa) --- */}
            <Modal 
                title="Đánh giá công việc này" 
                open={!!editingTask} 
                onCancel={() => setEditingTask(null)}
                onOk={handleSaveGrade}
                okText="Lưu đánh giá"
            >
                <div style={{ marginBottom: 15 }}>
                    <label style={{ display: 'block', marginBottom: 5, fontWeight: 500 }}>Điểm số (Thang 10):</label>
                    <InputNumber 
                        min={0} max={10} step={0.5} style={{ width: '100%' }} 
                        value={gradeInput.score} 
                        onChange={v => setGradeInput({...gradeInput, score: v})} 
                        placeholder="Nhập điểm..."
                    />
                </div>
                <div>
                    <label style={{ display: 'block', marginBottom: 5, fontWeight: 500 }}>Lời phê / Nhận xét:</label>
                    <Input.TextArea 
                        rows={3} 
                        value={gradeInput.comment} 
                        onChange={e => setGradeInput({...gradeInput, comment: e.target.value})} 
                        placeholder="Nhập nhận xét của giảng viên..."
                    />
                </div>
            </Modal>
        </>
    );
};

export default CheckpointModal;