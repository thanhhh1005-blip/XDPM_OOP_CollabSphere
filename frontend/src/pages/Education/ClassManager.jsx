import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, Select, Card, message, Tag, Space, Tooltip, List, Avatar, Upload, Popconfirm } from 'antd';
import { UserAddOutlined, EyeOutlined, UserOutlined, TeamOutlined, EditOutlined, DeleteOutlined, UploadOutlined } from '@ant-design/icons';

// Import các hàm từ ClassService
import { 
    getAllClasses, createClass, addStudentToClass, 
    getStudentsInClass, updateClass, deleteClass, importClasses, 
    removeStudentFromClass 
} from '../../services/classService'; 
import { getAllSubjects } from '../../services/subjectService';

// Import hàm từ UserService
import { getLecturers, getStudents } from '../../services/userService'; 

const ClassManager = () => {
    // --- 0. LẤY ROLE NGƯỜI DÙNG ---
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const isLecturer = user.role === 'LECTURER'; // true nếu là Giảng viên

    // --- STATE DỮ LIỆU ---
    const [classes, setClasses] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [lecturers, setLecturers] = useState([]); 
    const [allStudents, setAllStudents] = useState([]); 
    const [loading, setLoading] = useState(false);
    
    // --- STATE MODAL ---
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [isAddStudentModalOpen, setIsAddStudentModalOpen] = useState(false);
    const [isViewModalOpen, setIsViewModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);

    // --- STATE TẠM THỜI ---
    const [selectedClassId, setSelectedClassId] = useState(null);
    const [editingClass, setEditingClass] = useState(null);
    const [studentList, setStudentList] = useState([]); 

    // --- FORMS ---
    const [formCreate] = Form.useForm();
    const [formAddStudent] = Form.useForm();
    const [formEdit] = Form.useForm();

    // --- 1. LOAD DỮ LIỆU BAN ĐẦU ---
    useEffect(() => {
        fetchInitialData();
    }, []);

    const fetchInitialData = async () => {
        setLoading(true);
        try {
            const [classData, subjectData, lecturerData, studentData] = await Promise.all([
                getAllClasses(),
                getAllSubjects(),
                getLecturers(),
                getStudents() 
            ]);

            setClasses(classData);
            setSubjects(subjectData);
            setLecturers(lecturerData);
            setAllStudents(studentData);
        } catch (error) {
            console.error(error);
            message.error("Failed to connect to server!");
        } finally {
            setLoading(false);
        }
    };

    // --- 2. LOGIC LỌC SINH VIÊN ---
    const availableStudents = allStudents.filter(student => 
        !studentList.some(enrolled => enrolled.studentId === student.username)
    );

    // --- 3. CÁC HÀM XỬ LÝ (HANDLERS) ---
    const handleRemoveStudent = async (studentId) => {
        try {
            await removeStudentFromClass(selectedClassId, studentId);
            message.success("Removed student successfully!");
            handleViewStudents(selectedClassId);
        } catch (error) {
            message.error("Failed to remove student.");
        }
    };

    const openAddStudentModal = async (classId) => {
        setSelectedClassId(classId);
        formAddStudent.resetFields();
        try {
            const currentStudents = await getStudentsInClass(classId);
            setStudentList(currentStudents);
            setIsAddStudentModalOpen(true);
        } catch (error) {
            message.error("Lỗi khi tải dữ liệu lớp học.");
        }
    };

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

    const handleAddStudent = async (values) => {
        try {
            await addStudentToClass(selectedClassId, values.studentId);
            message.success(`Student added successfully!`);
            setIsAddStudentModalOpen(false);
            formAddStudent.resetFields();
            
            if (isViewModalOpen) {
                handleViewStudents(selectedClassId);
            }
        } catch (error) {
            message.error("Failed to add student.");
        }
    };

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

    const handleDeleteClass = async (id) => {
        try {
            await deleteClass(id);
            message.success("Deleted class successfully!");
            fetchInitialData();
        } catch (error) {
            message.error("Failed to delete class.");
        }
    };

    const openEditModal = (record) => {
        setEditingClass(record);
        formEdit.setFieldsValue({
            room: record.room,
            semester: record.semester,
            teacherId: record.teacherId, 
            subjectId: record.subjectId
        });
        setIsEditModalOpen(true);
    };

    const handleUpdateClass = async (values) => {
        try {
            const updatedData = { ...editingClass, ...values }; 
            await updateClass(editingClass.id, updatedData);
            message.success("Updated class successfully!");
            setIsEditModalOpen(false);
            fetchInitialData();
        } catch (error) {
            message.error("Failed to update class.");
        }
    };

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

    // --- 4. CẤU HÌNH CỘT BẢNG ---
    const columns = [
        { title: 'Code', dataIndex: 'code', key: 'code', render: (text) => <Tag color="blue">{text}</Tag> },
        { 
            title: 'Subject', key: 'subject',   
            render: (_, record) => (
                record.subject ? (
                    <span><b>{record.subject.name}</b><br/><small style={{color: '#888'}}>{record.subject.code}</small></span>
                ) : <Tag>{record.subjectId}</Tag>
            )
        },
        { 
            title: 'Lecturer', key: 'teacher',
            render: (_, record) => {
                const displayId = record.teacher?.username || record.teacherId;
                const displayName = record.teacher?.fullName;
                return (
                    <Space>
                        <Avatar src={record.teacher?.avatarUrl} style={{ backgroundColor: '#87d068' }} icon={<UserOutlined />} />
                        <div style={{ display: 'flex', flexDirection: 'column' }}>
                            <span style={{ fontWeight: 'bold', color: '#1677ff' }}>{displayId}</span>
                            {displayName && (<small style={{ color: '#999', fontSize: '11px' }}>{displayName}</small>)}
                        </div>
                    </Space>
                );
            }
        },
        { title: 'Room', dataIndex: 'room', key: 'room' },
        { title: 'Semester', dataIndex: 'semester', key: 'semester' },
        { 
            title: 'Actions', key: 'actions',
            render: (_, record) => (
                <Space>
                    {/* Nút Xem Sinh Viên: Ai cũng được thấy */}
                    <Tooltip title="View Students"><Button size="small" icon={<EyeOutlined />} onClick={() => handleViewStudents(record.id)} /></Tooltip>
                    
                    {/* 👇 ẨN TOÀN BỘ CÁC NÚT THAO TÁC (THÊM, SỬA, XÓA) NẾU LÀ GIẢNG VIÊN */}
                    {!isLecturer && (
                        <>
                            <Tooltip title="Enroll Student">
                                <Button size="small" type="dashed" icon={<UserAddOutlined />} onClick={() => openAddStudentModal(record.id)} />
                            </Tooltip>

                            <Tooltip title="Edit Class">
                                <Button size="small" type="primary" ghost icon={<EditOutlined />} onClick={() => openEditModal(record)} />
                            </Tooltip>

                            <Popconfirm title="Delete class?" onConfirm={() => handleDeleteClass(record.id)} okText="Yes" cancelText="No">
                                <Button size="small" danger icon={<DeleteOutlined />} />
                            </Popconfirm>
                        </>
                    )}
                </Space>
            )
        },
    ];

    // --- 5. GIAO DIỆN JSX ---
    return (
        <Card 
            title={<Space><TeamOutlined /> Class Management</Space>} 
            extra={
                <Space>
                    {!isLecturer && (
                        <>
                            <Upload customRequest={handleImport} showUploadList={false}>
                                <Button icon={<UploadOutlined />}>Import Excel</Button>
                            </Upload>
                            <Button type="primary" onClick={() => setIsCreateModalOpen(true)}>+ Create Class</Button>
                        </>
                    )}
                </Space>
            } 
            style={{ margin: 20 }}
        >
            <Table dataSource={classes} columns={columns} rowKey="id" loading={loading} />

            {/* Modal 1: Create Class */}
            <Modal title="Create New Class" open={isCreateModalOpen} onCancel={() => setIsCreateModalOpen(false)} onOk={() => formCreate.submit()} okText="Create">
                <Form form={formCreate} layout="vertical" onFinish={handleCreateClass}>
                    <Form.Item label="Class Code" name="code" rules={[{ required: true }]}><Input placeholder="SE104.O21" /></Form.Item>
                    <Form.Item label="Subject" name="subjectId" rules={[{ required: true }]}>
                        <Select placeholder="Select Subject" showSearch optionFilterProp="children">
                            {subjects.map(s => <Select.Option key={s.id} value={s.id}>{s.name} ({s.code})</Select.Option>)}
                        </Select>
                    </Form.Item>
                    <Form.Item label="Lecturer" name="teacherId" rules={[{ required: true }]}>
                        <Select placeholder="Select Lecturer" showSearch optionFilterProp="children">
                            {lecturers.map(t => <Select.Option key={t.id} value={t.username}>{t.fullName} ({t.username})</Select.Option>)}
                        </Select>
                    </Form.Item>
                    <Form.Item label="Room" name="room" rules={[{ required: true }]}><Input placeholder="B6-101" /></Form.Item>
                    <Form.Item label="Semester" name="semester" initialValue="HK1_2025"><Input /></Form.Item>
                </Form>
            </Modal>

            {/* Modal 2: Enroll Student */}
            <Modal title="Enroll Student" open={isAddStudentModalOpen} onCancel={() => setIsAddStudentModalOpen(false)} onOk={() => formAddStudent.submit()} okText="Enroll">
                <Form form={formAddStudent} layout="vertical" onFinish={handleAddStudent}>
                    <Form.Item 
                        label="Select Student" 
                        name="studentId" 
                        rules={[{ required: true, message: 'Please select a student' }]}
                    >
                        <Select 
                            placeholder={availableStudents.length > 0 ? "Type to search student..." : "All students are enrolled!"}
                            showSearch
                            optionFilterProp="children"
                            disabled={availableStudents.length === 0}
                        >
                            {availableStudents.map(student => (
                                <Select.Option key={student.id} value={student.username}>
                                    <Space>
                                        <Avatar size="small" src={student.avatarUrl} icon={<UserOutlined />} />
                                        <span>{student.fullName} ({student.username})</span>
                                    </Space>
                                </Select.Option>
                            ))}
                        </Select>
                        {availableStudents.length === 0 && (
                            <div style={{ color: 'green', marginTop: 8 }}>✅ All students are already in this class.</div>
                        )}
                    </Form.Item>
                </Form>
            </Modal>

            {/* Modal 3: View Students */}
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
                            <List.Item
                                actions={[
                                    // 👇 Ẩn nút xóa sinh viên nếu là Giảng viên
                                    !isLecturer && (
                                        <Popconfirm 
                                            title="Remove student?" 
                                            description={`Are you sure to remove ${item.studentId}?`}
                                            onConfirm={() => handleRemoveStudent(item.studentId)}
                                            okText="Yes"
                                            cancelText="No"
                                        >
                                            <Button danger size="small" icon={<DeleteOutlined />} type="text" />
                                        </Popconfirm>
                                    )
                                ]}
                            >
                                <List.Item.Meta 
                                    avatar={<Avatar style={{ backgroundColor: '#1890ff' }} icon={<UserOutlined />} />} 
                                    title={<b>{item.studentId}</b>} 
                                    description={`Enrollment ID: ${item.id}`} 
                                />
                            </List.Item>
                        )} 
                    />
                )}
            </Modal>

            {/* Modal 4: Update Class */}
            <Modal title="Update Class" open={isEditModalOpen} onCancel={() => setIsEditModalOpen(false)} onOk={() => formEdit.submit()} okText="Save Changes">
                <Form form={formEdit} layout="vertical" onFinish={handleUpdateClass}>
                    <Form.Item label="Class Code" style={{marginBottom: 10}}><Tag color="orange">{editingClass?.code}</Tag></Form.Item>
                    <Form.Item label="Subject" name="subjectId" rules={[{ required: true }]}><Select placeholder="Select Subject" showSearch optionFilterProp="children">{subjects.map(s => <Select.Option key={s.id} value={s.id}>{s.name} ({s.code})</Select.Option>)}</Select></Form.Item>
                    <Form.Item label="Lecturer" name="teacherId" rules={[{ required: true }]}><Select placeholder="Select Lecturer" showSearch optionFilterProp="children">{lecturers.map(t => <Select.Option key={t.id} value={t.username}>{t.fullName} ({t.username})</Select.Option>)}</Select></Form.Item>
                    <Form.Item label="Room" name="room" rules={[{ required: true }]}><Input /></Form.Item>
                    <Form.Item label="Semester" name="semester"><Input /></Form.Item>
                </Form>
            </Modal>
        </Card>
    );
};

export default ClassManager;