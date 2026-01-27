import React, { useState, useEffect, useCallback } from 'react';
import { 
    Layout, Typography, Button, Timeline, Card, Tag, 
    Space, message, Spin, Empty, Select,
    Modal, Form, Input, Upload, Result, Tooltip, DatePicker, InputNumber,
    Popconfirm, Table 
} from 'antd';
import { 
    RobotOutlined, PlusOutlined, EyeOutlined, CheckCircleOutlined, 
    LinkOutlined, UploadOutlined, UserOutlined, CrownOutlined, 
    TeamOutlined, DeleteOutlined, EditOutlined,
    UnorderedListOutlined, TrophyOutlined 
} from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import dayjs from 'dayjs'; 

// ✅ Import các Component con (Giữ nguyên như code của bạn)
import AiMilestoneModal from "../../components/AiMilestoneModal";
import CheckpointModal from "../../components/CheckpointModal"; 

const { Title, Paragraph, Text } = Typography;
const { Content } = Layout;

const MilestonePage = () => {
    const { classId } = useParams();
    const navigate = useNavigate();
    
    // ========================================================================
    // 1. KHAI BÁO STATE
    // ========================================================================
    
    const [milestones, setMilestones] = useState([]);
    const [myClasses, setMyClasses] = useState([]);
    const [checkpoints, setCheckpoints] = useState({}); 
    const [milestoneStats, setMilestoneStats] = useState({});
    const [loading, setLoading] = useState(false);

    // State Sửa Milestone
    const [isEditModalOpen, setIsEditModalOpen] = useState(false); 
    const [editingMilestone, setEditingMilestone] = useState(null);

    // State Logic & Phân quyền
    const [currentTeam, setCurrentTeam] = useState(null); 
    const [isLeader, setIsLeader] = useState(false);      
    const [hasTeamInClass, setHasTeamInClass] = useState(true); 
    const [projectSyllabus, setProjectSyllabus] = useState(""); 
    
    // Modal State
    const [isAiModalOpen, setIsAiModalOpen] = useState(false);
    const [isManualModalOpen, setIsManualModalOpen] = useState(false);
    const [isSubmitModalOpen, setIsSubmitModalOpen] = useState(false); 
    const [isViewSubmissionsOpen, setIsViewSubmissionsOpen] = useState(false);
    const [submissionList, setSubmissionList] = useState([]); 
    
    // Checkpoint/Subtask State
    const [isCheckpointModalOpen, setIsCheckpointModalOpen] = useState(false);
    const [selectedMilestoneForCP, setSelectedMilestoneForCP] = useState(null);
    const [teamMembers, setTeamMembers] = useState([]); 
    const [isGradeModalOpen, setIsGradeModalOpen] = useState(false);
    const [gradingTarget, setGradingTarget] = useState(null); 
    
    // Data Actions
    const [currentMilestoneId, setCurrentMilestoneId] = useState(null);
    const [fileList, setFileList] = useState([]); 
    
    // Forms
    const [submitForm] = Form.useForm();
    const [manualForm] = Form.useForm();
    const [editForm] = Form.useForm();
    const [gradeForm] = Form.useForm();

    // Auth Info
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const role = user.role || (user.roles && user.roles[0]);
    const isLecturer = role === 'LECTURER' || role === 'TEACHER';
    const token = localStorage.getItem('token');
    const myUsername = user.username || user.sub; 

    // ========================================================================
    // 2. LOGIC TẢI DỮ LIỆU
    // ========================================================================

    const fetchMilestones = useCallback(async () => {
        if (!classId) return;
        setLoading(true);
        try {
            const res = await axios.get(`http://localhost:8080/api/workspace/milestones/class/${classId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            let data = res.data.result || [];
            setMilestones(data.sort((a, b) => (a.weekNumber || 0) - (b.weekNumber || 0)));
        } catch (error) {
            console.error("Lỗi tải milestones:", error);
        } finally {
            setLoading(false);
        }
    }, [classId, token]);

    const fetchCheckpointStatus = useCallback(async (teamId) => {
        if (!teamId) return;
        try {
            const cpRes = await axios.get(`http://localhost:8080/api/workspace/milestones/checkpoint/status?teamId=${teamId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            const rawData = cpRes.data.result || [];
            console.log("🔥 Dữ liệu Checkpoint Raw:", rawData); // Debug xem có milestoneId không

            const map = {};
            rawData.forEach(cp => { 
                // Phòng trường hợp milestoneId nằm lồng trong object con hoặc viết thường
                const mId = cp.milestoneId || cp.milestone?.id;
                if (mId) {
                    map[mId] = cp; 
                }
            });
            
            console.log("✅ Checkpoint Map:", map); // Debug xem map có đúng key không
            setCheckpoints(map);
        } catch (e) { console.error(e); }
    }, [token]);

    const fetchMilestoneStats = useCallback(async () => {
        if (!classId) return;
        try {
            const res = await axios.get(`http://localhost:8080/api/workspace/milestones/class/${classId}/stats`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setMilestoneStats(res.data.result || {});
        } catch (e) { 
            console.warn("Lỗi tải stats (có thể API 404 chưa implement):", e); 
        }
    }, [classId, token]);

    useEffect(() => {
        const initWorkflow = async () => {
            setLoading(true);
            try {
                // 1. Load Classes
                const classRes = await axios.get(`http://localhost:8080/api/v1/teams/meta/classes`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                let classes = classRes.data.result || classRes.data || [];
                setMyClasses(classes);

                if (!classId && classes.length > 0) {
                    navigate(`/milestones/${classes[0].id}`, { replace: true });
                    return;
                }

                // 2. Logic Phân quyền & Load Team
                let activeTeamId = null;

                if (!isLecturer) {
                    const myTeamsRes = await axios.get(`http://localhost:8080/api/v1/teams/student/${myUsername}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    });
                    const myTeams = myTeamsRes.data.result || myTeamsRes.data || [];
                    const teamInThisClass = myTeams.find(t => t.classId == classId); // So sánh tương đối vì ID có thể là string/number

                    if (teamInThisClass) {
                        setCurrentTeam(teamInThisClass);
                        setHasTeamInClass(true);
                        activeTeamId = teamInThisClass.id;

                        // Load Members & Check Leader
                        try {
                            const memberRes = await axios.get(`http://localhost:8080/api/v1/teams/${teamInThisClass.id}/members`, {
                                headers: { Authorization: `Bearer ${token}` }
                            });
                            const members = memberRes.data.result || memberRes.data || [];
                            setTeamMembers(members); 

                            const me = members.find(m => m.userId === myUsername || m.username === myUsername);
                            if (me && (me.role === 'LEADER' || me.memberRole === 'LEADER')) {
                                setIsLeader(true);
                            } else {
                                setIsLeader(false);
                            }
                        } catch (err) { console.error("Lỗi lấy member:", err); }

                        // Load Syllabus
                        if (teamInThisClass.projectId) {
                            try {
                                const projectRes = await axios.get(`http://localhost:8080/api/v1/projects/${teamInThisClass.projectId}`, {
                                    headers: { Authorization: `Bearer ${token}` }
                                });
                                const content = projectRes.data.syllabus?.content || projectRes.data.description || "";
                                setProjectSyllabus(content);
                            } catch (e) { console.log("Không load được project info"); }
                        }
                    } else {
                        setHasTeamInClass(false);
                        setLoading(false);
                        return; 
                    }
                }

                // 3. Load Milestones
                await fetchMilestones();

                // 4. Load dữ liệu phụ thuộc
                if (isLecturer) {
                    await fetchMilestoneStats();
                } else if (activeTeamId) {
                    await fetchCheckpointStatus(activeTeamId);
                }

            } catch (error) {
                console.error("Lỗi khởi tạo:", error);
            } finally {
                setLoading(false);
            }
        };
        initWorkflow();
    }, [classId, navigate, token, myUsername, isLecturer, fetchMilestones]);

    // ========================================================================
    // 3. CÁC HÀM XỬ LÝ (HANDLERS)
    // ========================================================================

    const handleManualCreate = async (values) => {
        try {
            const payload = {
                title: values.title,
                description: values.description,
                weekNumber: values.weekNumber,
                startDate: values.dateRange ? values.dateRange[0].format('YYYY-MM-DD') + 'T00:00:00' : null,
                endDate: values.dateRange ? values.dateRange[1].format('YYYY-MM-DD') + 'T23:59:59' : null,
                classId: classId
            };

            await axios.post(`http://localhost:8080/api/workspace/milestones`, payload, {
                headers: { Authorization: `Bearer ${token}` }
            });

            message.success("Tạo cột mốc thành công!");
            setIsManualModalOpen(false);
            manualForm.resetFields();
            fetchMilestones(); 
        } catch (error) {
            message.error("Lỗi tạo cột mốc: " + (error.response?.data?.message || error.message));
        }
    };

    const openEditModal = (milestone) => {
        setEditingMilestone(milestone);
        setIsEditModalOpen(true);
        editForm.setFieldsValue({
            title: milestone.title,
            description: milestone.description,
            weekNumber: milestone.weekNumber,
            dateRange: [
                milestone.startDate ? dayjs(milestone.startDate) : null,
                milestone.endDate ? dayjs(milestone.endDate) : null
            ]
        });
    };

    const handleUpdate = async (values) => {
        try {
            const payload = {
                ...editingMilestone,
                title: values.title,
                description: values.description,
                weekNumber: values.weekNumber,
                startDate: values.dateRange ? values.dateRange[0].format('YYYY-MM-DD') + 'T00:00:00' : null,
                endDate: values.dateRange ? values.dateRange[1].format('YYYY-MM-DD') + 'T23:59:59' : null,
            };

            await axios.put(`http://localhost:8080/api/workspace/milestones/${editingMilestone.id}`, payload, {
                headers: { Authorization: `Bearer ${token}` }
            });

            message.success("Cập nhật thành công!");
            setIsEditModalOpen(false);
            fetchMilestones(); 
        } catch (error) {
            message.error("Lỗi cập nhật: " + (error.response?.data?.message || error.message));
        }
    };

    const handleDelete = async (id) => {
        try {
            await axios.delete(`http://localhost:8080/api/workspace/milestones/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            message.success("Đã xóa cột mốc!");
            fetchMilestones(); 
        } catch (error) {
            message.error("Lỗi xóa: " + (error.response?.data?.message || error.message));
        }
    };

    const handleViewSubmissions = async (mId) => {
        setIsViewSubmissionsOpen(true); 
        setSubmissionList([]); 
        try {
            const res = await axios.get(`http://localhost:8080/api/workspace/milestones/${mId}/checkpoints`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSubmissionList(res.data.result || []);
        } catch (e) {
            message.error("Lỗi tải bài nộp");
        }
    };

    const openSubmitModal = (mId) => {
        setCurrentMilestoneId(mId);
        setIsSubmitModalOpen(true);
        setFileList([]);
        submitForm.resetFields();
    };

    const handleSubmitConfirm = async (values) => {
        if (!currentTeam) { message.error("Lỗi: Không tìm thấy team!"); return; }

        const formData = new FormData();
        formData.append('milestoneId', currentMilestoneId);
        formData.append('teamId', currentTeam.id);
        formData.append('teamName', currentTeam.name);
        console.log("teamName =", currentTeam.name);
        formData.append('note', values.note || '');
        if (fileList.length > 0) {
            formData.append('file', fileList[0].originFileObj || fileList[0]);
        }

        try {
            await axios.post(`http://localhost:8080/api/workspace/milestones/checkpoint/submit-file`, formData, {
                headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'multipart/form-data' } 
            });
            message.success("Nộp bài thành công!");
            setIsSubmitModalOpen(false);
            // Refresh lại trạng thái thay vì reload trang
            await fetchCheckpointStatus(currentTeam.id);
        } catch (e) { message.error("Lỗi nộp bài!"); }
    };

    const handleCompleteMilestone = async (milestoneId) => {
        try {
            const res = await axios.post(`http://localhost:8080/api/workspace/milestones/complete/${milestoneId}`, null, {
                params: { teamId: currentTeam.id },
                headers: { Authorization: `Bearer ${token}` }
            });
            
            const updatedCheckpoint = res.data.result; 
            message.success(res.data.message);

            setCheckpoints(prev => ({
                ...prev,
                [milestoneId]: updatedCheckpoint 
            }));

        } catch (error) {
            message.error(error.response?.data?.message || "Lỗi xử lý trạng thái!");
        }
    };

    const openGradeModal = (record) => {
        setGradingTarget(record);
        setIsGradeModalOpen(true);
        gradeForm.setFieldsValue({
            score: record.score,
            feedback: record.feedback
        });
    };

    const handleGradeSubmit = async (values) => {
        try {
            await axios.put(`http://localhost:8080/api/workspace/milestones/checkpoint/grade`, {
                score: values.score,
                feedback: values.feedback
            }, {
                params: { milestoneId: gradingTarget.milestoneId, teamId: gradingTarget.teamId },
                headers: { Authorization: `Bearer ${token}` }
            });
            message.success("Đã lưu điểm!");
            setIsGradeModalOpen(false);
            handleViewSubmissions(gradingTarget.milestoneId);
        } catch (e) {
            message.error("Lỗi chấm điểm");
        }
    };

    // ========================================================================
    // 4. CHUẨN BỊ DỮ LIỆU RENDER TIMELINE (KHẮC PHỤC LỖI DEPRECATED)
    // ========================================================================

    const timelineItems = milestones.map((item) => {
        // Lấy thông tin trạng thái từ API
        const myCP = checkpoints[item.id];
        const isSubmitted = (myCP?.status === 'SUBMITTED') || (myCP?.submissionUrl && myCP.submissionUrl.length > 0);
        const isCompleted = myCP?.status === 'COMPLETED'; 

        return {
            key: item.id,
            color: isCompleted ? "green" : (isSubmitted ? "blue" : "gray"),
            children: (
                <Card 
                    size="small" 
                    title={
                        <Space>
                            <Tag color="geekblue">Tuần {item.weekNumber}</Tag>
                            <Text strong>{item.title}</Text>
                        </Space>
                    }
                    style={{ 
                        border: isCompleted ? '2px solid #52c41a' : (isSubmitted ? '1px solid #1890ff' : undefined),
                        background: isCompleted ? '#f6ffed' : '#fff'
                    }}
                >
                    <Paragraph>{item.description}</Paragraph>
                    {myCP?.score != null && (
                        <div style={{ 
                            marginTop: 10, 
                            marginBottom: 10, 
                            padding: 12, 
                            background: '#f6ffed', 
                            border: '1px solid #b7eb8f', 
                            borderRadius: 6 
                        }}>
                            <Space direction="vertical" style={{ width: '100%' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                    <Text strong style={{ color: '#389e0d', fontSize: 15 }}>
                                        <CheckCircleOutlined /> KẾT QUẢ ĐÁNH GIÁ GIAI ĐOẠN
                                    </Text>
                                    <Tag color="red" style={{ fontSize: 16, padding: '5px 10px', fontWeight: 'bold' }}>
                                        {myCP.score} / 10
                                    </Tag>
                                </div>
                                {myCP.feedback && (
                                    <Text type="secondary">
                                        <span style={{ fontWeight: 600 }}>Giảng viên nhận xét:</span> {myCP.feedback}
                                    </Text>
                                )}
                            </Space>
                        </div>
                    )}
                    {/* ✅ FIX 1: Chỉ hiển thị trạng thái cá nhân nếu KHÔNG phải Giảng viên */}
                    {!isLecturer && (
                        <div style={{ marginBottom: 12 }}>
                            {isCompleted ? (
                                <Space>
                                    <Tag icon={<CheckCircleOutlined />} color="success" style={{ fontWeight: 'bold', padding: '5px 10px' }}>
                                        ĐÃ HOÀN THÀNH
                                    </Tag>
                                    
                                    {isLeader && (
                                        <Button 
                                            size="small" danger type="text"
                                            onClick={() => handleCompleteMilestone(item.id)}
                                        >
                                            (Hoàn tác)
                                        </Button>
                                    )}
                                </Space>
                            ) : (
                                <Space>
                                    {isSubmitted ? <Tag color="processing">Đã nộp file</Tag> : <Tag color="default">Chưa nộp file</Tag>}
                                    
                                    {isLeader && (
                                        <Button 
                                            type="primary" size="small" ghost 
                                            icon={<TrophyOutlined />}
                                            onClick={() => handleCompleteMilestone(item.id)}
                                        >
                                            Đánh dấu Hoàn thành
                                        </Button>
                                    )}
                                </Space>
                            )}
                        </div>
                    )}

                    <div style={{ display: 'flex', justifyContent: 'space-between', borderTop: '1px solid #eee', paddingTop: 8 }}>
                        {isLecturer ? (
                            <Space style={{ width: '100%', justifyContent: 'space-between' }}>
                                {/* Giao diện cho GIẢNG VIÊN */}
                                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                                    {(milestoneStats[item.id] && milestoneStats[item.id] > 0) ? (
                                        <Tag color="blue" icon={<CheckCircleOutlined />}>
                                            Đã có {milestoneStats[item.id]} nhóm nộp
                                        </Tag>
                                    ) : (
                                        <Tag color="default">Chưa có bài nộp</Tag>
                                    )}
                                </div>

                                <Space>
                                    <Button type="link" icon={<EyeOutlined />} onClick={() => handleViewSubmissions(item.id)}>Xem bài</Button>
                                    <Button type="text" icon={<EditOutlined />} onClick={() => openEditModal(item)} style={{ color: '#faad14' }}>Sửa</Button>
                                    <Popconfirm title="Xóa?" onConfirm={() => handleDelete(item.id)}>
                                        <Button type="text" danger icon={<DeleteOutlined />}>Xóa</Button>
                                    </Popconfirm>
                                </Space>
                            </Space>
                        ) : (
                            <Space style={{ width: '100%', justifyContent: 'space-between' }}>
                                {/* Giao diện cho SINH VIÊN */}
                                <Space>
                                    {myCP?.submissionUrl && (
                                        <a href={myCP.submissionUrl} target="_blank" rel="noreferrer"><LinkOutlined /> Xem file</a>
                                    )}
                                    
                                    <Button 
                                        icon={<UnorderedListOutlined />} 
                                        onClick={() => {
                                            setSelectedMilestoneForCP(item);
                                            setIsCheckpointModalOpen(true);
                                        }}
                                    >
                                        Checkpoints (Việc nhỏ)
                                    </Button>
                                </Space>

                                {isLeader && !isCompleted && (
                                    <Button type="primary" size="small" icon={<UploadOutlined />} onClick={() => openSubmitModal(item.id)}>
                                        {isSubmitted ? "Nộp lại" : "Nộp báo cáo"}
                                    </Button>
                                )}
                                
                                {!isLeader && (
                                    <Tooltip title="Chỉ Nhóm trưởng mới được nộp">
                                        <Button size="small" disabled>Nộp báo cáo</Button>
                                    </Tooltip>
                                )}
                            </Space>
                        )}
                    </div>
                </Card>
            )
        };
    });

    // ========================================================================
    // 5. RENDER CHÍNH
    // ========================================================================

    if (!isLecturer && !hasTeamInClass) {
        return (
            <Layout style={{ minHeight: '100vh', background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Result
                    status="403"
                    icon={<TeamOutlined style={{ color: '#faad14' }} />}
                    title="Bạn chưa tham gia nhóm nào trong lớp này"
                    subTitle="Vui lòng tham gia nhóm để xem lộ trình."
                    extra={<Button type="primary" onClick={() => navigate('/teams')}>Đến trang Chọn Nhóm</Button>}
                />
            </Layout>
        );
    }
    console.log("Dữ liệu bảng:", submissionList);
    return (
        <Layout style={{ minHeight: '100vh', background: '#f5f7fa' }}>
            <Content style={{ padding: '24px', maxWidth: 1200, margin: '0 auto', width: '100%' }}>
                
                {/* HEADER */}
                <div style={{ background: '#fff', padding: 16, borderRadius: 8, marginBottom: 20, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                        <Title level={4} style={{ margin: 0 }}>🚩 Lộ trình: {currentTeam?.name || "Dự án lớp học"}</Title> 
                        <Space style={{ marginTop: 4 }}>
                            <Select 
                                value={Number(classId)}
                                onChange={(id) => navigate(`/milestones/${id}`)}
                                style={{ width: 180 }}
                                options={myClasses.map(c => ({ label: c.classCode, value: c.id }))}
                            />
                            {!isLecturer && (
                                <Tag color={isLeader ? "gold" : "cyan"} icon={isLeader ? <CrownOutlined /> : <UserOutlined />}>
                                    {isLeader ? "Nhóm Trưởng" : "Thành Viên"}
                                </Tag>
                            )}
                        </Space>
                    </div>
                    {isLecturer && (
                        <Space>
                            <Button icon={<PlusOutlined />} onClick={() => setIsManualModalOpen(true)}>Thêm cột mốc</Button>
                            <Button type="primary" icon={<RobotOutlined />} onClick={() => setIsAiModalOpen(true)}>AI Gợi ý</Button>
                        </Space>
                    )}
                </div>

                {/* TIMELINE */}
                <Spin spinning={loading}>
                    <div style={{ maxWidth: 900, margin: '0 auto' }}>
                        {milestones.length === 0 ? (
                            <Empty description="Chưa có lộ trình" />
                        ) : (
                            // ✅ FIX 2: Sử dụng prop items thay vì children để fix lỗi deprecated
                            <Timeline items={timelineItems} />
                        )}
                    </div>
                </Spin>

                {/* MODAL 1: SV NỘP BÀI */}
                <Modal title="Nộp báo cáo (Upload MinIO)" open={isSubmitModalOpen} onCancel={() => setIsSubmitModalOpen(false)} footer={null}>
                    <Form form={submitForm} layout="vertical" onFinish={handleSubmitConfirm}>
                        <Form.Item label="File đính kèm (Zip/Docx)" required>
                            <Upload beforeUpload={(file) => { setFileList([file]); return false; }} onRemove={() => setFileList([])} fileList={fileList} maxCount={1}>
                                <Button icon={<UploadOutlined />}>Chọn file từ máy tính</Button>
                            </Upload>
                        </Form.Item>
                        <Form.Item label="Ghi chú" name="note"><Input.TextArea rows={3} /></Form.Item>
                        <div style={{ textAlign: 'right' }}>
                            <Button type="primary" htmlType="submit" disabled={fileList.length === 0}>Xác nhận nộp</Button>
                        </div>
                    </Form>
                </Modal>

                {/* MODAL 2: TẠO THỦ CÔNG */}
                <Modal title="Thêm cột mốc mới" open={isManualModalOpen} onCancel={() => setIsManualModalOpen(false)} footer={null}>
                    <Form form={manualForm} layout="vertical" onFinish={handleManualCreate}>
                        <Form.Item label="Tiêu đề" name="title" rules={[{ required: true }]}><Input /></Form.Item>
                        <Space style={{ display: 'flex' }} align="baseline">
                            <Form.Item label="Tuần số" name="weekNumber" rules={[{ required: true }]}><InputNumber min={1} /></Form.Item>
                            <Form.Item label="Thời hạn" name="dateRange"><DatePicker.RangePicker /></Form.Item>
                        </Space>
                        <Form.Item label="Mô tả" name="description"><Input.TextArea rows={3} /></Form.Item>
                        <div style={{ textAlign: 'right' }}><Button type="primary" htmlType="submit">Tạo mới</Button></div>
                    </Form>
                </Modal>
                
                {/* MODAL 3: SỬA MILESTONE */}
                <Modal title="Cập nhật cột mốc" open={isEditModalOpen} onCancel={() => setIsEditModalOpen(false)} footer={null}>
                    <Form form={editForm} layout="vertical" onFinish={handleUpdate}>
                        <Form.Item label="Tiêu đề" name="title" rules={[{ required: true }]}><Input /></Form.Item>
                        <Space style={{ display: 'flex' }} align="baseline">
                            <Form.Item label="Tuần số" name="weekNumber" rules={[{ required: true }]}><InputNumber min={1} /></Form.Item>
                            <Form.Item label="Thời hạn" name="dateRange"><DatePicker.RangePicker /></Form.Item>
                        </Space>
                        <Form.Item label="Mô tả" name="description"><Input.TextArea rows={3} /></Form.Item>
                        <div style={{ textAlign: 'right' }}>
                            <Button onClick={() => setIsEditModalOpen(false)} style={{ marginRight: 8 }}>Hủy</Button>
                            <Button type="primary" htmlType="submit">Lưu thay đổi</Button>
                        </div>
                    </Form>
                </Modal>
                
                {/* MODAL 4: XEM BÀI NỘP */}
                <Modal title="Danh sách bài nộp & Chấm điểm" open={isViewSubmissionsOpen} onCancel={() => setIsViewSubmissionsOpen(false)} footer={null} width={900}>
                    <Table 
                        dataSource={submissionList} 
                        rowKey="id" 
                        columns={[
                            { title: 'Nhóm',dataIndex: 'teamName', render: (text, record) => (<Tag color="blue">{text ? text : record.teamId}</Tag>) },
                            { title: 'Ngày nộp', dataIndex: 'submittedAt', render: d => d ? dayjs(d).format('HH:mm DD/MM') : '' },
                            { title: 'File', dataIndex: 'submissionUrl', render: u => u ? <a href={u} target="_blank">Tải file</a> : <Text type="secondary">Trống</Text> },
                            { title: 'Điểm', dataIndex: 'score', render: s => s ? <Tag color="green">{s}</Tag> : <Tag>Chưa chấm</Tag> },
                            { title: 'Nhận xét', dataIndex: 'feedback', ellipsis: true },
                            { 
                                title: 'Thao tác', 
                                render: (_, record) => (
                                    <Button type="primary" size="small" onClick={() => openGradeModal(record)}>
                                        Chấm điểm
                                    </Button>
                                ) 
                            }
                        ]} 
                    />
                </Modal>

                {/* MODAL 5: CHẤM ĐIỂM */}
                <Modal title={`Chấm điểm cho nhóm: ${gradingTarget?.teamId}`} open={isGradeModalOpen} onCancel={() => setIsGradeModalOpen(false)} footer={null}>
                    <Form form={gradeForm} onFinish={handleGradeSubmit} layout="vertical">
                        <Form.Item name="score" label="Điểm số (0-10)" rules={[{ required: true }]}>
                            <InputNumber min={0} max={10} step={0.1} style={{ width: '100%' }} />
                        </Form.Item>
                        <Form.Item name="feedback" label="Nhận xét/Góp ý">
                            <Input.TextArea rows={4} placeholder="Nhập nhận xét của giảng viên..." />
                        </Form.Item>
                        <div style={{ textAlign: 'right' }}>
                            <Button type="primary" htmlType="submit">Lưu kết quả</Button>
                        </div>
                    </Form>
                </Modal>

                {/* MODAL 6: AI */}
                <AiMilestoneModal 
                    isOpen={isAiModalOpen} 
                    onClose={() => setIsAiModalOpen(false)} 
                    classId={classId} 
                    initialSyllabus={projectSyllabus}
                    onSuccess={() => { setIsAiModalOpen(false); fetchMilestones(); }} 
                />

                {/* MODAL 7: CHECKPOINTS MANAGER */}
                <CheckpointModal 
                    isOpen={isCheckpointModalOpen}
                    onClose={() => setIsCheckpointModalOpen(false)}
                    milestone={selectedMilestoneForCP}
                    teamId={currentTeam?.id}
                    teamMembers={teamMembers}
                    isLeader={isLeader}
                    currentUser={myUsername}
                    isLecturer={isLecturer}
                />

            </Content>
        </Layout>
    );
};

export default MilestonePage;