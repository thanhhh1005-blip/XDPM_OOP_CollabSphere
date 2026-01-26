import React, { useState, useEffect } from 'react';
import { Modal, Input, Button, InputNumber, Form, message, List, Card, Tag, Space, Typography, Select, DatePicker } from 'antd';
import { RobotOutlined, ThunderboltOutlined, CheckCircleOutlined, BookOutlined, CalendarOutlined } from '@ant-design/icons';
import axios from 'axios';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { Text, Title } = Typography;
const { Option } = Select;

const AiMilestoneModal = ({ isOpen, onClose, classId, onSuccess, initialSyllabus }) => {
    const [loading, setLoading] = useState(false);
    const [generatedData, setGeneratedData] = useState([]);
    const [projects, setProjects] = useState([]); 
    const [form] = Form.useForm();

    // 1. Init Data
    useEffect(() => {
        if (isOpen) {
            fetchProjects();
            form.setFieldsValue({
                weeks: 15,
                syllabus: initialSyllabus || '',
                startDate: dayjs() // Mặc định là ngày hôm nay
            });
            setGeneratedData([]);
        }
    }, [isOpen, initialSyllabus]);

    const fetchProjects = async () => {
        try {
            const token = localStorage.getItem('token');
            const res = await axios.get(`http://localhost:8080/api/v1/projects`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setProjects(res.data.result || res.data || []);
        } catch (error) { console.error(error); }
    };

    const handleProjectChange = (projectId) => {
        const selected = projects.find(p => p.id === projectId);
        if (selected) {
            const content = selected.syllabus?.content || selected.description || "";
            form.setFieldsValue({ syllabus: content });
            message.info("Đã lấy Syllabus từ: " + selected.title);
        }
    };

    // 2. Xử lý AI & Tự động tính ngày
    const handleGenerate = async (values) => {
        setLoading(true);
        try {
            const token = localStorage.getItem('token');
            
            // Bước A: Gọi AI để lấy danh sách (JSON thuần)
            // Lưu ý: Gọi endpoint generate (không save) để ta xử lý ngày tháng ở Frontend trước
            // Hoặc gọi endpoint cũ nhưng ta sẽ ghi đè ngày tháng lại.
            
            // Ở đây mình gọi endpoint generate-and-save như cũ, nhưng lưu ý:
            // Backend đang trả về list milestone nhưng chưa có ngày.
            // Ta cần chặn việc lưu ở backend, hoặc update lại sau. 
            // ĐỂ ĐƠN GIẢN: Ta sẽ tự tính ngày ở đây rồi gửi batch-save thủ công.
            
            // Gọi AI lấy JSON (giả lập endpoint chỉ generate)
            // Nếu bạn chưa tách API, ta cứ dùng API cũ, nhưng ta sẽ gửi đè ngày tháng ở bước saveBatch.
            
            const aiRes = await axios.post('http://localhost:8080/api/ai/milestones/generate-and-save', {
                syllabusContent: values.syllabus,
                durationWeeks: values.weeks,
                classId: classId
            }, { headers: { Authorization: `Bearer ${token}` } });

            const rawMilestones = aiRes.data.result || aiRes.data;

            if (!rawMilestones || rawMilestones.length === 0) {
                message.warning('AI không trả về kết quả.');
                setLoading(false);
                return;
            }

            // Bước B: LOGIC TỰ ĐỘNG TÍNH NGÀY (QUAN TRỌNG 🟢)
            const startDateProject = values.startDate; // Ngày bắt đầu user chọn
            
            const milestonesWithDates = rawMilestones.map(m => {
                // Logic: Ngày bắt đầu của Milestone = Ngày dự án + (Tuần - 1) * 7
                const start = startDateProject.add((m.weekNumber - 1) * 7, 'day');
                const end = start.add(6, 'day'); // Milestone kéo dài 1 tuần

                return {
                    ...m,
                    startDate: start.format('YYYY-MM-DDTHH:mm:ss'), // Format chuẩn Java
                    endDate: end.format('YYYY-MM-DDTHH:mm:ss'),
                    classId: classId
                };
            });

            // Bước C: Gửi lại danh sách ĐÃ CÓ NGÀY xuống Backend để lưu đè/lưu mới
            await axios.post(`http://localhost:8080/api/workspace/milestones/batch-save?classId=${classId}`, milestonesWithDates, {
                headers: { Authorization: `Bearer ${token}` }
            });

            message.success('AI đã tạo lộ trình & Tự động tính ngày thành công!');
            setGeneratedData(milestonesWithDates);
            if (onSuccess) onSuccess();

        } catch (error) {
            console.error(error);
            message.error('Lỗi: ' + (error.response?.data?.message || error.message));
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal
            title={<Space><RobotOutlined style={{ color: '#722ed1' }} /><span>AI Creating Milestone</span></Space>}
            open={isOpen} onCancel={onClose} footer={null} width={750}
        >
            {!generatedData.length && (
                <Form layout="vertical" form={form} onFinish={handleGenerate}>
                    <Form.Item label="Lấy Syllabus từ Project" style={{ marginBottom: 12 }}>
                        <Select placeholder="Chọn dự án..." onChange={handleProjectChange} allowClear>
                            {projects.map(p => (
                                <Option key={p.id} value={p.id}>{p.title}</Option>
                            ))}
                        </Select>
                    </Form.Item>

                    <Space style={{ display: 'flex', width: '100%' }} align="baseline">
                        <Form.Item label="Tổng tuần" name="weeks" initialValue={15} style={{ flex: 1 }}>
                            <InputNumber min={1} max={52} style={{ width: '100%' }} />
                        </Form.Item>
                        
                        {/* 👇 Ô CHỌN NGÀY BẮT ĐẦU (MỚI) */}
                        <Form.Item 
                            label="Ngày bắt đầu học kỳ/dự án" 
                            name="startDate" 
                            rules={[{ required: true, message: 'Cần chọn ngày bắt đầu để tính lịch' }]}
                            style={{ flex: 1 }}
                            tooltip="AI sẽ dựa vào ngày này để tự động điền Ngày bắt đầu/Kết thúc cho từng cột mốc."
                        >
                            <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
                        </Form.Item>
                    </Space>

                    <Form.Item label="Nội dung Syllabus" name="syllabus" rules={[{ required: true }]}>
                        <TextArea rows={6} placeholder="Nội dung..." />
                    </Form.Item>

                    <Button type="primary" htmlType="submit" loading={loading} block icon={<ThunderboltOutlined />} style={{ background: '#722ed1' }}>
                        Tạo Lộ trình & Tính ngày tự động
                    </Button>
                </Form>
            )}

            {generatedData.length > 0 && (
                <div style={{ textAlign: 'center' }}>
                    <CheckCircleOutlined style={{ fontSize: 40, color: '#52c41a' }} />
                    <Title level={4}>Thành công!</Title>
                    <div style={{ maxHeight: '400px', overflowY: 'auto', textAlign: 'left' }}>
                        <List dataSource={generatedData} renderItem={item => (
                            <Card size="small" style={{ marginBottom: 8 }}>
                                <List.Item.Meta
                                    avatar={<Tag color="blue">Tuần {item.weekNumber}</Tag>}
                                    title={item.title}
                                    description={
                                        <div>
                                            {item.description} <br/>
                                            <Text type="secondary" style={{ fontSize: 12 }}>
                                                <CalendarOutlined /> {dayjs(item.startDate).format('DD/MM')} - {dayjs(item.endDate).format('DD/MM/YYYY')}
                                            </Text>
                                        </div>
                                    }
                                />
                            </Card>
                        )} />
                    </div>
                    <Button onClick={() => { setGeneratedData([]); form.resetFields(); onClose(); }}>Đóng</Button>
                </div>
            )}
        </Modal>
    );
};

export default AiMilestoneModal;