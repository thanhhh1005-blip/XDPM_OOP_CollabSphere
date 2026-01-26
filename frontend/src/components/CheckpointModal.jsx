import React, { useState, useEffect } from 'react';
import { Modal, List, Checkbox, Button, Input, Select, Progress, Tag, message, Popconfirm, Empty } from 'antd';
import { PlusOutlined, DeleteOutlined, CheckSquareOutlined, UserOutlined } from '@ant-design/icons';
import axios from 'axios';

const CheckpointModal = ({ isOpen, onClose, milestone, teamId, teamMembers, isLeader, currentUser }) => {
    const [subTasks, setSubTasks] = useState([]);
    const [newTitle, setNewTitle] = useState("");
    const [assignedUser, setAssignedUser] = useState(null);
    const [loading, setLoading] = useState(false);

    const token = localStorage.getItem('token');

    // Load dữ liệu khi mở Modal
    useEffect(() => {
        if (isOpen && milestone && teamId) {
            fetchSubTasks();
        }
    }, [isOpen, milestone, teamId]);

    const fetchSubTasks = async () => {
        try {
            // Gọi API lấy subtask theo milestone và team
            const res = await axios.get(`http://localhost:8080/api/workspace/subtasks`, {
                params: { milestoneId: milestone.id, teamId: teamId },
                headers: { Authorization: `Bearer ${token}` }
            });
            setSubTasks(res.data.result || []);
        } catch (error) {
            console.error(error);
        }
    };

    // Thêm Checkpoint mới
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

    // Đánh dấu hoàn thành
    const handleToggle = async (task) => {
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

    // Xóa
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

    // Tính %
    const completedCount = subTasks.filter(t => t.completed).length;
    const totalCount = subTasks.length;
    // Nếu totalCount = 0 thì percent = 0, tránh chia cho 0
    const percent = (totalCount && totalCount > 0) ? Math.round((completedCount / totalCount) * 100) : 0;

    return (
        <Modal 
            title={<span><CheckSquareOutlined /> Checkpoints: {milestone?.title}</span>} 
            open={isOpen} 
            onCancel={onClose} 
            footer={null}
            width={700}
        >
            {/* 1. THANH TIẾN ĐỘ */}
            <div style={{ marginBottom: 20 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 5 }}>
                    <strong>Tiến độ hoàn thành:</strong>
                    <span>{completedCount}/{totalCount} checkpoints</span>
                </div>
                <Progress percent={percent} status={percent === 100 ? "success" : "active"} strokeColor={{ from: '#108ee9', to: '#87d068' }} />
            </div>

            {/* 2. FORM THÊM (CHỈ LEADER) */}
            {isLeader && (
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

            {/* 3. DANH SÁCH CHECKPOINT */}
            <List
                locale={{ emptyText: <Empty description="Chưa có checkpoint nào" /> }}
                dataSource={subTasks}
                renderItem={item => (
                    <List.Item
                        actions={[
                            isLeader && (
                                <Popconfirm title="Xóa checkpoint này?" onConfirm={() => handleDelete(item.id)}>
                                    <Button type="text" danger icon={<DeleteOutlined />} />
                                </Popconfirm>
                            )
                        ]}
                    >
                        <List.Item.Meta
                            avatar={
                                <Checkbox 
                                    checked={item.completed} 
                                    onChange={() => handleToggle(item)}
                                    disabled={!isLeader && item.assignedTo !== currentUser}
                                />
                            }
                            title={
                                <span style={{ 
                                    textDecoration: item.completed ? 'line-through' : 'none', 
                                    color: item.completed ? '#999' : '#000',
                                    fontWeight: 500
                                }}>
                                    {item.title}
                                </span>
                            }
                            description={
                                <Tag icon={<UserOutlined />} color={item.assignedTo ? (item.assignedTo === currentUser ? "blue" : "cyan") : "default"}>
                                    {item.assignedTo || "Chưa phân công"}
                                </Tag>
                            }
                        />
                    </List.Item>
                )}
            />
        </Modal>
    );
};

export default CheckpointModal;