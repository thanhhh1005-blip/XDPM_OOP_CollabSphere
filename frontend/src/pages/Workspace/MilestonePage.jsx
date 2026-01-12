import React, { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Card, Timeline, Tag, Button, Modal, Input, message, Typography, DatePicker, Divider, List, Row, Col } from 'antd';
import { PlusOutlined, CheckCircleOutlined, SendOutlined, FlagOutlined, PaperClipOutlined } from '@ant-design/icons';
import axios from 'axios';

const { Title, Text } = Typography;

const MilestonePage = () => {
    const [userRole] = useOutletContext(); // Lấy vai trò người dùng từ MainLayout
    const [milestones, setMilestones] = useState([]);
    const [isCreateMsOpen, setIsCreateMsOpen] = useState(false);
    const [isCreateCpOpen, setIsCreateCpOpen] = useState(false);
    const [isSubmitOpen, setIsSubmitOpen] = useState(false);

    const [selectedMsId, setSelectedMsId] = useState(null);
    const [selectedCpId, setSelectedCpId] = useState(null);

    const [newMs, setNewMs] = useState({ title: '', description: '', dueDate: null });
    const [newCpTitle, setNewCpTitle] = useState('');
    const [submitUrl, setSubmitUrl] = useState('');

    const API_BASE = 'http://localhost:8080/api/workspace/milestones';

    const fetchData = async () => {
        try {
            const res = await axios.get(API_BASE);
            setMilestones(res.data.data || []);
        } catch (e) { message.error("Lỗi tải dữ liệu"); }
    };

    useEffect(() => { fetchData(); }, []);

    const handleCreateMs = async () => {
        try {
            await axios.post(API_BASE, newMs);
            message.success("Đã tạo cột mốc!");
            setIsCreateMsOpen(false);
            fetchData();
        } catch (e) { message.error("Lỗi tạo Milestone"); }
    };

    const handleCreateCp = async () => {
        try {
            await axios.post(`${API_BASE}/${selectedMsId}/checkpoints`, { title: newCpTitle });
            message.success("Đã thêm điểm nộp bài!");
            setIsCreateCpOpen(false);
            setNewCpTitle('');
            fetchData();
        } catch (e) { message.error("Lỗi tạo Checkpoint"); }
    };

    const handleSubmitCp = async () => {
        try {
            await axios.put(`${API_BASE}/checkpoints/${selectedCpId}/submit?url=${submitUrl}`);
            message.success("Đã nộp bài thành công!");
            setIsSubmitOpen(false);
            setSubmitUrl('');
            fetchData();
        } catch (e) { message.error("Lỗi nộp bài"); }
    };

    return (
        <div style={{ padding: '30px', background: '#f5f7fa', minHeight: '100vh' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
                <Title level={2}><FlagOutlined /> Lộ trình dự án PBL</Title>
                {userRole === 'LECTURER' && (
                    <Button type="primary" icon={<PlusOutlined />} onClick={() => setIsCreateMsOpen(true)}>
                        Thêm Giai Đoạn (Dành cho GV)
                    </Button>
                )}
            </div>

            <Row gutter={24}>
                <Col span={18}>
                    <Card bordered={false} style={{ borderRadius: '15px', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
                        <Timeline mode="left" style={{ marginTop: '20px' }}>
                            {milestones.map(m => (
                                <Timeline.Item 
                                    key={m.id} 
                                    label={<b style={{fontSize: '14px'}}>{new Date(m.dueDate).toLocaleDateString('vi-VN')}</b>}
                                    dot={<FlagOutlined style={{ fontSize: '16px' }} />}
                                    color="blue"
                                >
                                    <Card 
                                        title={<span style={{fontSize: '18px'}}>{m.title}</span>} 
                                        extra={userRole === 'LECTURER' && <Button type="link" onClick={() => { setSelectedMsId(m.id); setIsCreateCpOpen(true); }}>+ Thêm điểm nộp</Button>}
                                        style={{ marginBottom: '20px', borderRadius: '10px', border: '1px solid #e8e8e8' }}
                                    >
                                        <p style={{ color: '#666' }}>{m.description}</p>
                                        
                                        <List
                                            header={<div style={{fontWeight:'bold'}}>📍 Danh sách bài nộp:</div>}
                                            dataSource={m.checkpoints || []}
                                            renderItem={cp => (
                                                <List.Item actions={[
                                                    cp.status === 'SUBMITTED' ? ( <div style={{textAlign:'right'}}>
                                                    <Tag color="green">Đã nộp</Tag><br/>
                                                    <a href={cp.submissionUrl} target="_blank">Xem lại bài nộp</a> </div>): 
                                                    (
                                                      // CHỈ SINH VIÊN MỚI THẤY NÚT NỘP BÀI
                                                      userRole === 'STUDENT' ? 
                                                      <Button type="primary" onClick={() => { setSelectedCpId(cp.id); setIsSubmitOpen(true); }}>Nộp bài</Button> :
                                                      <Text type="secondary">Chờ SV nộp bài...</Text>
                                                    )
                                                ]}>
                                                    <div>
                                                        <Text>{cp.title}</Text>
                                                        {cp.submissionUrl && <div style={{fontSize:'12px'}}><a href={cp.submissionUrl} target="_blank">Xem bài nộp</a></div>}
                                                    </div>
                                                </List.Item>
                                            )}
                                        />
                                    </Card>
                                </Timeline.Item>
                            ))}
                        </Timeline>
                    </Card>
                </Col>
            </Row>

            {/* Modal tạo Milestone */}
            <Modal title="Thêm Giai Đoạn Mới" open={isCreateMsOpen} onOk={handleCreateMs} onCancel={() => setIsCreateMsOpen(false)}>
                <Input placeholder="Tên giai đoạn" style={{marginBottom: 10}} onChange={e => setNewMs({...newMs, title: e.target.value})} />
                <Input.TextArea placeholder="Mô tả" style={{marginBottom: 10}} onChange={e => setNewMs({...newMs, description: e.target.value})} />
                <DatePicker style={{width:'100%'}} onChange={(d, s) => setNewMs({...newMs, dueDate: s})} />
            </Modal>

            {/* Modal tạo Checkpoint */}
            <Modal title="Thêm Điểm Nộp Bài" open={isCreateCpOpen} onOk={handleCreateCp} onCancel={() => setIsCreateCpOpen(false)}>
                <Input placeholder="Tên bài tập/báo cáo cần nộp" value={newCpTitle} onChange={e => setNewCpTitle(e.target.value)} />
            </Modal>

            {/* Modal Nộp bài */}
            <Modal title="Nộp Bài Tập" open={isSubmitOpen} onOk={handleSubmitCp} onCancel={() => setIsSubmitOpen(false)}>
                <p>Link bài nộp (Github/Drive):</p>
                <Input placeholder="https://..." value={submitUrl} onChange={e => setSubmitUrl(e.target.value)} />
            </Modal>
        </div>
    );
};

export default MilestonePage;