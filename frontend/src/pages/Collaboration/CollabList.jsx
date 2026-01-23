import React, { useState, useEffect } from 'react';
import { Card, Tabs, List, Avatar, Tag, Typography, message, Spin, Empty } from 'antd';
import { TeamOutlined, BankOutlined, ArrowRightOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const { Title, Text } = Typography;

const CollabList = () => {
    const navigate = useNavigate();
    
    // 1. Lấy user và role từ localStorage
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const role = user.role || (user.roles && user.roles[0]); 

    const [myTeams, setMyTeams] = useState([]);
    const [myClasses, setMyClasses] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            const token = localStorage.getItem('token');
            const headers = { Authorization: `Bearer ${token}` };

            try {
                // --- 1. LẤY DANH SÁCH LỚP (Phân quyền) ---
                let classUrl = "";
                if (role === 'LECTURER' || role === 'TEACHER') {
                    classUrl = `http://localhost:8080/api/classes/teacher/${user.username}`;
                } else {
                    classUrl = `http://localhost:8080/api/classes/student/${user.username || user.studentId}`;
                }

                const resClass = await axios.get(classUrl, { headers });
                const classesData = resClass.data || [];
                setMyClasses(classesData);

                // --- 2. LẤY DANH SÁCH NHÓM (Logic thông minh cho GV) ---
                if (role === 'LECTURER' || role === 'TEACHER') {
                    // ==> NẾU LÀ GIẢNG VIÊN: Lấy tất cả team thuộc các lớp mình dạy
                    if (classesData.length > 0) {
                        // Chạy song song các API lấy team của từng lớp
                        const teamPromises = classesData.map(cls => 
                            axios.get(`http://localhost:8080/api/v1/teams/class/${cls.id}`, { headers })
                                 .then(res => {
                                     // Gắn thêm thông tin classCode vào team để hiển thị đẹp hơn
                                     const teams = res.data.result || [];
                                     return teams.map(t => ({ ...t, classCode: cls.code || cls.className }));
                                 })
                                 .catch(() => []) 
                        );

                        const teamsArrays = await Promise.all(teamPromises);
                        const allTeams = teamsArrays.flat(); // Gộp thành 1 danh sách duy nhất
                        setMyTeams(allTeams);
                    } else {
                        setMyTeams([]);
                    }
                } else {
                    // ==> NẾU LÀ SINH VIÊN: Lấy team cá nhân (Logic cũ)
                    const resTeam = await axios.get(`http://localhost:8080/api/v1/teams/student/${user.username}`, { headers });
                    setMyTeams(resTeam.data?.result || resTeam.data || []);
                }

            } catch (e) { 
                console.error("Lỗi tải danh sách Collab:", e); 
                // message.error("Không thể tải dữ liệu cộng tác");
            } finally {
                setLoading(false);
            }
        };

        if (user.username) {
            fetchData();
        }
    }, [user.username, role]);

    const enterRoom = (type, id, name) => {
        // type: 'TEAM' hoặc 'CLASS'
        navigate(`/collaboration/${type}_${id}?name=${name}`);
    };

    // --- Giao diện Tab Nhóm ---
    const TeamListTab = () => (
        <List
            dataSource={myTeams}
            locale={{ emptyText: <Empty description={role === 'LECTURER' ? "Các lớp bạn dạy chưa có nhóm nào" : "Bạn chưa tham gia nhóm nào"} /> }}
            renderItem={item => (
                <Card hoverable style={{ marginBottom: 15, borderRadius: 12, borderLeft: '5px solid #1890ff' }} onClick={() => enterRoom('TEAM', item.id, item.name)}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <List.Item.Meta
                            avatar={<Avatar size="large" icon={<TeamOutlined />} style={{ backgroundColor: '#1890ff' }} />}
                            title={<b style={{ fontSize: '16px' }}>{item.name}</b>}
                            description={
                                <div>
                                    <Tag color="blue">{item.classCode || 'Dự án'}</Tag>
                                    {/* Nếu muốn hiện thêm tên Leader hoặc số thành viên thì thêm ở đây */}
                                </div>
                            }
                        />
                        <ArrowRightOutlined style={{ fontSize: '20px', color: '#1890ff' }} />
                    </div>
                </Card>
            )}
        />
    );

    // --- Giao diện Tab Lớp ---
    const ClassListTab = () => (
        <List
            dataSource={myClasses}
            locale={{ emptyText: <Empty description="Không tìm thấy lớp học" /> }}
            renderItem={item => (
                <Card hoverable style={{ marginBottom: 15, borderRadius: 12, borderLeft: '5px solid #52c41a' }} onClick={() => enterRoom('CLASS', item.id, item.code)}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <List.Item.Meta
                            avatar={<Avatar size="large" icon={<BankOutlined />} style={{ backgroundColor: '#52c41a' }} />}
                            title={<b style={{ fontSize: '16px' }}>Lớp {item.code || item.className}</b>}
                            description={<Text type="secondary">Môn: {item.subject?.name || item.subjectId}</Text>}
                        />
                        <ArrowRightOutlined style={{ fontSize: '20px', color: '#52c41a' }} />
                    </div>
                </Card>
            )}
        />
    );

    return (
        <div style={{ padding: '30px', maxWidth: '800px', margin: '0 auto' }}>
            <Title level={2}>🤝 Không gian cộng tác</Title>
            <Text type="secondary" style={{display: 'block', marginBottom: 20}}>
                {role === 'LECTURER' ? 'Quản lý các phòng họp và bảng trắng của Lớp/Nhóm' : 'Tham gia thảo luận cùng nhóm và lớp học'}
            </Text>

            <Spin spinning={loading}>
                <Tabs defaultActiveKey="2" centered items={[
                    {
                        key: '2',
                        label: <span><BankOutlined /> Lớp học ({myClasses.length})</span>,
                        children: <ClassListTab />
                    },
                    {
                        key: '1',
                        label: <span><TeamOutlined /> Nhóm ({myTeams.length})</span>,
                        children: <TeamListTab />
                    }
                    
                ]} />
            </Spin>
        </div>
    );
};

export default CollabList;