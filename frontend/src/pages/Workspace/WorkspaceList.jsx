import React, { useState, useEffect } from 'react';
import { Card, Tabs, List, Avatar, Tag, Typography, message, Spin, Empty, Button } from 'antd';
import { TeamOutlined, BankOutlined, ArrowRightOutlined, BookOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const { Title, Text } = Typography;

const WorkspaceList = () => {
    const navigate = useNavigate();
    // Lấy thông tin user từ localStorage
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const role = user.role || (user.roles && user.roles[0]);

    const [loading, setLoading] = useState(false);
    
    const [myTeams, setMyTeams] = useState([]);
    const [myClasses, setMyClasses] = useState([]);
    const [classMap, setClassMap] = useState({});

    
    const fetchClassesMap = async () => {
        try {
            const token = localStorage.getItem('token');
            const res = await axios.get("http://localhost:8080/api/v1/teams/meta/classes", {
                headers: { Authorization: `Bearer ${token}` }
            });
            const data = res.data?.result ?? res.data ?? [];
            const map = {};
            data.forEach((c) => {
                map[String(c.id)] = c.code || c.classCode;
            });
            setClassMap(map);
        } catch (e) { console.error(e); }
    };

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            const token = localStorage.getItem('token');
            const headers = { Authorization: `Bearer ${token}` };

            try {
                // --- 1. LẤY DANH SÁCH LỚP (Logic cũ) ---
                let classUrl = "";
                if (role === 'LECTURER' || role === 'TEACHER') {
                    classUrl = `http://localhost:8080/api/classes/teacher/${user.username}`;
                } else {
                    classUrl = `http://localhost:8080/api/classes/student/${user.username || user.studentId}`;
                }

                const resClass = await axios.get(classUrl, { headers });
                const classesData = resClass.data || []; 
                setMyClasses(classesData);

                // --- 2. LẤY DANH SÁCH TEAM (Logic MỚI) ---
                if (role === 'LECTURER' || role === 'TEACHER') {
                    // ==> LOGIC CHO GIẢNG VIÊN: Lấy tất cả team của các lớp mình dạy
                    if (classesData.length > 0) {
                        const teamPromises = classesData.map(cls => 
                            axios.get(`http://localhost:8080/api/v1/teams/class/${cls.id}`, { headers })
                                 .then(res => res.data.result || []) // Giả sử backend trả về ApiResponse chuẩn
                                 .catch(() => []) // Nếu lỗi lớp nào thì bỏ qua lớp đó, không sập app
                        );

                        const teamsArrays = await Promise.all(teamPromises);
                        
                        const allTeams = teamsArrays.flat();
                        setMyTeams(allTeams);
                    } else {
                        setMyTeams([]);
                    }
                } else {
                    // ==> LOGIC CHO SINH VIÊN (Giữ nguyên)
                    const resTeam = await axios.get(`http://localhost:8080/api/v1/teams/student/${user.username}`, { headers });
                    setMyTeams(resTeam.data?.result || resTeam.data || []);
                }

            } catch (e) { 
                console.error("Lỗi tải dữ liệu:", e);
                message.error("Có lỗi khi tải dữ liệu lớp/nhóm");
            } finally {
                setLoading(false);
            }
        };

        if (user.username) {
            fetchData();
            fetchClassesMap();
        }
    }, [user.username, role]);

    // HÀM CHUYỂN HƯỚNG
    const enterWorkspace = async (type, entityId) => {
        const token = localStorage.getItem('token');
        const headers = { Authorization: `Bearer ${token}` };

        try {
            let url = "";
            if (type === 'TEAM') {
                url = `http://localhost:8080/api/workspace/workspaces/team/${entityId}`;
            } else {
                url = `http://localhost:8080/api/workspace/workspaces/class/${entityId}`;
            }

            const res = await axios.get(url, { headers });

            if (res.data && res.data.code === 1000 && res.data.result) {
                const workspaceId = res.data.result.id;
                message.success("Đang kết nối không gian làm việc...");
                
                if (type === 'TEAM') {
                    navigate(`/workspace/${workspaceId}`);
                } else {
                    // Chuyển hướng GV vào Class Board
                    navigate(`/workspace/${workspaceId}/class/${entityId}/board`);
                }

            } else {
                message.warning("Đang khởi tạo không gian làm việc...");
            }
        } catch (e) {
            console.error("Lỗi kết nối Workspace:", e);
            message.error("Chưa kích hoạt Workspace cho mục này");
        }
    };
    const ClassListTab = () => (
        <List
            dataSource={myClasses}
            locale={{ emptyText: <Empty description="Bạn chưa có lớp học nào" /> }}
            renderItem={item => (
                <Card hoverable style={{ marginBottom: 15, borderRadius: 12, borderLeft: '5px solid #722ed1' }} 
                    onClick={() => enterWorkspace('CLASS', item.id)}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <List.Item.Meta
                            avatar={<Avatar size="large" icon={<BankOutlined />} style={{ backgroundColor: '#722ed1' }} />}
                            title={<b style={{ fontSize: '16px' }}>{item.className || item.code}</b>}
                            description={
                                <div>
                                    <Tag color="purple">Lớp học</Tag>
                                    <Tag color="default"><BookOutlined /> {item.semester || 'HK'}</Tag>
                                    {/* Hiển thị khác biệt cho GV và SV */}
                                    {role === 'LECTURER' 
                                        ? <Tag color="gold">Giảng dạy</Tag> 
                                        : <Text type="secondary" style={{marginLeft: 8}}>GV: {item.teacherId}</Text>
                                    }
                                </div>
                            }
                        />
                        <ArrowRightOutlined style={{ fontSize: '20px', color: '#722ed1' }} />
                    </div>
                </Card>
            )}
        />
    );
    // --- GIAO DIỆN ---
    const TeamListTab = () => (
        <List
            dataSource={myTeams}
            locale={{ emptyText: <Empty description={role === 'LECTURER' ? "Giảng viên vui lòng vào Lớp học để xem các nhóm" : "Bạn chưa tham gia nhóm nào"} /> }}
            renderItem={item => (
                <Card hoverable style={{ marginBottom: 15, borderRadius: 12, borderLeft: '5px solid #1890ff' }} 
                    onClick={() => enterWorkspace('TEAM', item.id)}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <List.Item.Meta
                            avatar={<Avatar size="large" icon={<TeamOutlined />} style={{ backgroundColor: '#1890ff' }} />}
                            title={<b style={{ fontSize: '16px' }}>{item.name}</b>}
                            description={<Tag color="green">Lớp: {classMap[String(item.classId)] || '...'}</Tag>}
                        />
                        <ArrowRightOutlined style={{ fontSize: '20px', color: '#1890ff' }} />
                    </div>
                </Card>
            )}
        />
    );

    

    const tabItems = [
        { key: '1', label: <span><BankOutlined /> Lớp học của tôi</span>, children: <ClassListTab /> },
        { key: '2', label: <span><TeamOutlined /> Nhóm của tôi</span>, children: <TeamListTab /> },
        
    ];

    return (
        <div style={{ padding: '30px', maxWidth: '900px', margin: '0 auto' }}>
            <Title level={2}>Không gian làm việc</Title>
            <Text type="secondary">
                Xin chào {user.fullName || user.username} ({role === 'LECTURER' ? 'Giảng viên' : 'Sinh viên'})
            </Text>
            
            <Spin spinning={loading}>
                <Card style={{ marginTop: 20, borderRadius: 12, boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
                    <Tabs defaultActiveKey="1" items={tabItems} size="large" />
                </Card>
            </Spin>
        </div>
    );
};

export default WorkspaceList;