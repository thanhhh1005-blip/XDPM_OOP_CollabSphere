import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, InputNumber, Card, message, Tag, Space, Tooltip, Popconfirm, Upload } from 'antd';
import { BookOutlined, EditOutlined, DeleteOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons';
// Import Service
import { getAllSubjects, createSubject, updateSubject, deleteSubject, importSubjects } from '../../services/subjectService';

const SubjectManager = () => {
    const [subjects, setSubjects] = useState([]);
    const [loading, setLoading] = useState(false);

    // --- STATE MODALS ---
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    
    // --- STATE DATA ---
    const [editingSubject, setEditingSubject] = useState(null);

    // --- FORM INSTANCES ---
    const [formCreate] = Form.useForm();
    const [formEdit] = Form.useForm();

    useEffect(() => {
        fetchSubjects();
    }, []);

    const fetchSubjects = async () => {
        setLoading(true);
        try {
            const data = await getAllSubjects();
            setSubjects(data);
        } catch (error) {
            message.error("Failed to load subjects!");
        } finally {
            setLoading(false);
        }
    };

    // --- 1. HANDLE CREATE ---
    const handleCreate = async (values) => {
        try {
            await createSubject(values);
            message.success("Subject created successfully!");
            setIsCreateModalOpen(false);
            formCreate.resetFields();
            fetchSubjects();
        } catch (error) {
            message.error("Failed to create subject (Code might already exist)!");
        }
    };

    // --- 2. HANDLE UPDATE ---
    const openEditModal = (record) => {
        setEditingSubject(record);
        // Fill old data to form
        formEdit.setFieldsValue({
            name: record.name,
            credits: record.credits
        });
        setIsEditModalOpen(true);
    };

    const handleUpdate = async (values) => {
        try {
            // Merge old data (id, code) with new data (name, credits)
            const updatedData = { ...editingSubject, ...values };
            await updateSubject(editingSubject.id, updatedData);
            
            message.success("Subject updated successfully!");
            setIsEditModalOpen(false);
            fetchSubjects();
        } catch (error) {
            message.error("Failed to update subject!");
        }
    };

    // --- 3. HANDLE DELETE ---
    const handleDelete = async (id) => {
        try {
            await deleteSubject(id);
            message.success("Subject deleted successfully!");
            fetchSubjects();
        } catch (error) {
            message.error("Failed to delete (Subject might be used in a class)!");
        }
    };

    // --- 4. HANDLE IMPORT EXCEL ---
    const handleImport = async ({ file, onSuccess, onError }) => {
        try {
            await importSubjects(file);
            message.success(`File ${file.name} imported successfully!`);
            onSuccess("ok");
            fetchSubjects();
        } catch (error) {
            message.error("Import failed! Please check your Excel file.");
            onError(error);
        }
    };

    // --- TABLE COLUMNS ---
    const columns = [
        { 
            title: 'Code', 
            dataIndex: 'code', 
            key: 'code', 
            render: (text) => <Tag color="blue">{text}</Tag> 
        },
        { 
            title: 'Subject Name', 
            dataIndex: 'name', 
            key: 'name',
            render: (text) => <b>{text}</b>
        },
        { 
            title: 'Credits', 
            dataIndex: 'credits', 
            key: 'credits',
            align: 'center',
            render: (num) => <Tag color="green">{num}</Tag>
        },
        { 
            title: 'Actions', 
            key: 'actions',
            align: 'center',
            render: (_, record) => (
                <Space>
                    {/* Edit Button */}
                    <Tooltip title="Edit Subject">
                        <Button 
                            type="primary" 
                            ghost 
                            icon={<EditOutlined />} 
                            onClick={() => openEditModal(record)} 
                        />
                    </Tooltip>

                    {/* Delete Button */}
                    <Popconfirm
                        title="Delete this subject?"
                        description="This action cannot be undone!"
                        onConfirm={() => handleDelete(record.id)}
                        okText="Yes, Delete"
                        cancelText="Cancel"
                        okButtonProps={{ danger: true }}
                    >
                        <Button danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                </Space>
            )
        },
    ];

    return (
        <Card 
            title={<Space><BookOutlined /> Subject Management</Space>} 
            extra={
                <Space>
                    {/* Import Button */}
                    <Upload customRequest={handleImport} showUploadList={false} accept=".xlsx, .xls">
                        <Button icon={<UploadOutlined />}>Import Excel</Button>
                    </Upload>

                    {/* Create Button */}
                    <Button type="primary" icon={<PlusOutlined />} onClick={() => setIsCreateModalOpen(true)}>
                         Create Subject
                    </Button>
                </Space>
            } 
            style={{ margin: 20 }}
        >
            <Table 
                dataSource={subjects} 
                columns={columns} 
                rowKey="id" 
                loading={loading} 
                pagination={{ pageSize: 8 }}
            />

            {/* --- MODAL CREATE --- */}
            <Modal 
                title="Create New Subject" 
                open={isCreateModalOpen} 
                onCancel={() => setIsCreateModalOpen(false)} 
                onOk={() => formCreate.submit()} 
                okText="Create"
            >
                <Form form={formCreate} layout="vertical" onFinish={handleCreate}>
                    <Form.Item label="Subject Code" name="code" rules={[{ required: true, message: 'Please enter subject code!' }]}>
                        <Input placeholder="Ex: SE104" />
                    </Form.Item>
                    <Form.Item label="Subject Name" name="name" rules={[{ required: true, message: 'Please enter subject name!' }]}>
                        <Input placeholder="Ex: Software Engineering" />
                    </Form.Item>
                    <Form.Item label="Credits" name="credits" rules={[{ required: true, message: 'Please enter credits!' }]}>
                        <InputNumber min={0} max={10} style={{ width: '100%' }} />
                    </Form.Item>
                </Form>
            </Modal>

            {/* --- MODAL EDIT --- */}
            <Modal 
                title="Update Subject" 
                open={isEditModalOpen} 
                onCancel={() => setIsEditModalOpen(false)} 
                onOk={() => formEdit.submit()} 
                okText="Save Changes"
            >
                <Form form={formEdit} layout="vertical" onFinish={handleUpdate}>
                    {/* Display Code (Read-only) */}
                    <Form.Item label="Subject Code">
                        <Tag color="orange" style={{ fontSize: 14 }}>{editingSubject?.code}</Tag>
                        <span style={{ fontSize: 12, color: '#999', marginLeft: 8 }}>(Cannot be changed)</span>
                    </Form.Item>

                    <Form.Item label="Subject Name" name="name" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item label="Credits" name="credits" rules={[{ required: true }]}>
                        <InputNumber min={0} max={10} style={{ width: '100%' }} />
                    </Form.Item>
                </Form>
            </Modal>
        </Card>
    );
};

export default SubjectManager;