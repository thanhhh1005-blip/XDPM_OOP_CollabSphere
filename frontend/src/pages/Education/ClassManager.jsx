import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, Select, Card, message, Tag, Space, Tooltip, List, Avatar, Upload, Popconfirm } from 'antd';
import { UserAddOutlined, EyeOutlined, UserOutlined, TeamOutlined, EditOutlined, DeleteOutlined, UploadOutlined, InboxOutlined } from '@ant-design/icons';
// Đảm bảo bạn đã export các hàm này bên file service nhé
import { 
    getAllClasses, 
    createClass, 
    addStudentToClass, 
    getStudentsInClass, 
    updateClass, 
    deleteClass, 
    importClasses 
} from '../../services/classService'; 
import { getAllSubjects } from '../../services/subjectService';

const ClassManager = () => {
    const [classes, setClasses] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [loading, setLoading] = useState(false);
    
    // --- CÁC MODAL STATE ---
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [isAddStudentModalOpen, setIsAddStudentModalOpen] = useState(false);
    const [isViewModalOpen, setIsViewModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false); // <--- MỚI: Modal sửa

    // --- DỮ LIỆU TẠM ---
    const [selectedClassId, setSelectedClassId] = useState(null);
    const [editingClass, setEditingClass] = useState(null); // <--- MỚI: Lưu lớp đang sửa
    const [studentList, setStudentList] = useState([]);

    // --- FORMS ---
    const [formCreate] = Form.useForm();
    const [formAddStudent] = Form.useForm();
    const [formEdit] = Form.useForm(); // <--- MỚI: Form sửa

    useEffect(() => {
        fetchInitialData();
    }, []);

    const fetchInitialData = async () => {
        setLoading(true);
        try {
            const [classData, subjectData] = await Promise.all([
                getAllClasses(),
                getAllSubjects()
            ]);
            setClasses(classData);
            setSubjects(subjectData);
        } catch (error) {
            message.error("Failed to connect to server!");
        } finally {
            setLoading(false);
        }
    };

    // --- 1. LOGIC TẠO LỚP ---
    const handleCreateClass = async (values) => {
        try {
            await createClass(values);
            message.success("Class created successfully!");
            setIsCreateModalOpen(false);
            formCreate.resetFields();
            fetchInitialData(); 
        } catch (error) {
            message.error("Failed to create class.");
        }
    };

    // --- 2. LOGIC THÊM SINH VIÊN ---
    const handleAddStudent = async (values) => {
        try {
            await addStudentToClass(selectedClassId, values.studentId);
            message.success(`Student ${values.studentId} added successfully!`);
            setIsAddStudentModalOpen(false);
            formAddStudent.resetFields();
            if (isViewModalOpen) {
                handleViewStudents(selectedClassId);
            }
        } catch (error) {
            message.error("Failed to add student (Maybe duplicate?).");
        }
    };

    // --- 3. LOGIC XEM DANH SÁCH ---
    const handleViewStudents = async (classId) => {
        setSelectedClassId(classId);
        try {
            const students = await getStudentsInClass(classId);
            setStudentList(students);
            setIsViewModalOpen(true);
        } catch (error) {
            message.error("Could not load student list.");
        }
    };

    // --- 4. LOGIC XÓA LỚP (MỚI) ---
    const handleDeleteClass = async (id) => {
        try {
            await deleteClass(id);
            message.success("Deleted class successfully!");
            fetchInitialData();
        } catch (error) {
            message.error("Failed to delete class.");
        }
    };

    // --- 5. LOGIC SỬA LỚP (MỚI) ---
    const openEditModal = (record) => {
        setEditingClass(record);
        formEdit.setFieldsValue({
            room: record.room,
            semester: record.semester,
            teacherId: record.teacherId,
            subjectId: record.subjectId
            // code thường không cho sửa, nếu muốn sửa thì thêm vào đây
        });
        setIsEditModalOpen(true);
    };

    const handleUpdateClass = async (values) => {
        try {
            // Merge các giá trị cũ và mới (giữ lại code cũ nếu không sửa)
            const updatedData = { ...editingClass, ...values }; 
            await updateClass(editingClass.id, updatedData);
            
            message.success("Updated class successfully!");
            setIsEditModalOpen(false);
            fetchInitialData();
        } catch (error) {
            message.error("Failed to update class.");
        }
    };

    // --- 6. LOGIC IMPORT EXCEL (MỚI) ---
    const handleImport = async ({ file, onSuccess, onError }) => {
        try {
            await importClasses(file);
            message.success(`${file.name} imported successfully`);
            onSuccess("ok");
            fetchInitialData();
        } catch (error) {
            message.error(`${file.name} import failed.`);
            onError(error);
        }
    };

    // --- CẤU HÌNH CỘT BẢNG ---
    const columns = [
        { title: 'Code', dataIndex: 'code', key: 'code', render: (text) => <Tag color="blue">{text}</Tag> },
        { title: 'Subject ID', dataIndex: 'subjectId', key: 'subjectId' },
        { title: 'Teacher', dataIndex: 'teacherId', key: 'teacherId' },
        { title: 'Room', dataIndex: 'room', key: 'room' },
        { 
            title: 'Actions', 
            key: 'actions',
            render: (_, record) => (
                <Space>
                    {/* Nút Xem SV */}
                    <Tooltip title="View Students">
                        <Button icon={<EyeOutlined />} onClick={() => handleViewStudents(record.id)} />
                    </Tooltip>

                    {/* Nút Thêm SV */}
                    <Tooltip title="Enroll Student">
                        <Button type="dashed" icon={<UserAddOutlined />} onClick={() => {
                            setSelectedClassId(record.id);
                            setIsAddStudentModalOpen(true);
                        }} />
                    </Tooltip>

                    {/* Nút Sửa (MỚI) */}
                    <Tooltip title="Edit Class">
                        <Button type="primary" ghost icon={<EditOutlined />} onClick={() => openEditModal(record)} />
                    </Tooltip>

                    {/* Nút Xóa (MỚI) */}
                    <Popconfirm 
                        title="Delete the class"
                        description="Are you sure to delete this class?"
                        onConfirm={() => handleDeleteClass(record.id)}
                        okText="Yes"
                        cancelText="No"
                    >
                        <Button danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                </Space>
            )
        },
    ];

    return (
        <Card 
            title={<Space><TeamOutlined /> Class Management</Space>} 
            extra={
                <Space>
                    {/* Nút Import (MỚI) */}
                    <Upload customRequest={handleImport} showUploadList={false}>
                        <Button icon={<UploadOutlined />}>Import Excel</Button>
                    </Upload>
                    
                    <Button type="primary" onClick={() => setIsCreateModalOpen(true)}>+ Create Class</Button>
                </Space>
            } 
            style={{ margin: 20 }}
        >
            <Table dataSource={classes} columns={columns} rowKey="id" loading={loading} />

            {/* Modal 1: Tạo Lớp */}
            <Modal title="Create New Class" open={isCreateModalOpen} onCancel={() => setIsCreateModalOpen(false)} onOk={() => formCreate.submit()} okText="Create">
                <Form form={formCreate} layout="vertical" onFinish={handleCreateClass}>
                    <Form.Item label="Class Code" name="code" rules={[{ required: true }]}><Input placeholder="SE104.O21" /></Form.Item>
                    <Form.Item label="Subject" name="subjectId" rules={[{ required: true }]}>
                        <Select placeholder="Select Subject">
                            {subjects.map(s => <Select.Option key={s.id} value={s.id}>{s.name} ({s.code})</Select.Option>)}
                        </Select>
                    </Form.Item>
                    <Form.Item label="Teacher ID" name="teacherId" rules={[{ required: true }]}><Input placeholder="GV001" /></Form.Item>
                    <Form.Item label="Room" name="room" rules={[{ required: true }]}><Input placeholder="B6-101" /></Form.Item>
                    <Form.Item label="Semester" name="semester" initialValue="HK1_2025"><Input /></Form.Item>
                </Form>
            </Modal>

            {/* Modal 2: Thêm Sinh Viên */}
            <Modal title="Enroll Student" open={isAddStudentModalOpen} onCancel={() => setIsAddStudentModalOpen(false)} onOk={() => formAddStudent.submit()} okText="Enroll">
                <Form form={formAddStudent} layout="vertical" onFinish={handleAddStudent}>
                    <Form.Item label="Student ID" name="studentId" rules={[{ required: true, message: 'Please enter Student ID' }]}>
                        <Input prefix={<UserOutlined />} placeholder="Ex: SV2021001" />
                    </Form.Item>
                </Form>
            </Modal>

            {/* Modal 3: Xem Danh Sách */}
            <Modal 
                title={`Student List (Class ID: ${selectedClassId})`} 
                open={isViewModalOpen} 
                onCancel={() => setIsViewModalOpen(false)} 
                footer={[<Button key="close" onClick={() => setIsViewModalOpen(false)}>Close</Button>]}
            >
                {studentList.length === 0 ? (
                    <div style={{ textAlign: 'center', padding: 20, color: '#999' }}>No students enrolled yet.</div>
                ) : (
                    <List
                        itemLayout="horizontal"
                        dataSource={studentList}
                        renderItem={(item) => (
                            <List.Item>
                                <List.Item.Meta
                                    avatar={<Avatar style={{ backgroundColor: '#1890ff' }} icon={<UserOutlined />} />}
                                    title={<b>{item.studentId}</b>}
                                    description={`Enrollment Record ID: ${item.id}`}
                                />
                            </List.Item>
                        )}
                    />
                )}
            </Modal>

            {/* Modal 4: Sửa Lớp (MỚI) */}
            <Modal title="Update Class" open={isEditModalOpen} onCancel={() => setIsEditModalOpen(false)} onOk={() => formEdit.submit()} okText="Save Changes">
                <Form form={formEdit} layout="vertical" onFinish={handleUpdateClass}>
                    {/* Không cho sửa Mã Lớp (Code) vì nó là định danh, hoặc tuỳ bạn */}
                    <Form.Item label="Class Code" style={{marginBottom: 10}}>
                        <Tag color="orange">{editingClass?.code}</Tag> 
                        <span style={{fontSize: 12, color: '#999'}}>(Cannot change Code)</span>
                    </Form.Item>

                    <Form.Item label="Subject" name="subjectId" rules={[{ required: true }]}>
                        <Select placeholder="Select Subject">
                            {subjects.map(s => <Select.Option key={s.id} value={s.id}>{s.name} ({s.code})</Select.Option>)}
                        </Select>
                    </Form.Item>
                    <Form.Item label="Teacher ID" name="teacherId" rules={[{ required: true }]}><Input /></Form.Item>
                    <Form.Item label="Room" name="room" rules={[{ required: true }]}><Input /></Form.Item>
                    <Form.Item label="Semester" name="semester"><Input /></Form.Item>
                </Form>
            </Modal>
        </Card>
    );
};

export default ClassManager;