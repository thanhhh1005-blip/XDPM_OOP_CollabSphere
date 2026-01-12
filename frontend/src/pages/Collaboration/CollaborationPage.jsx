import React, { useEffect, useState } from 'react';
import {
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  message,
  Popconfirm
} from 'antd';
import {
  PlusOutlined,
  DeleteOutlined,
  ReloadOutlined
} from '@ant-design/icons';

import {
  getCollaborations,
  createCollaboration,
  deleteCollaboration
} from '../../services/collaborationService';

const CollaborationPage = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);

  const [form] = Form.useForm();

  /* ===================== LOAD ===================== */
  const fetchData = async () => {
    try {
      setLoading(true);
      const res = await getCollaborations();
      setData(res);
    } catch {
      message.error('Không tải được collaboration');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  /* ===================== CREATE ===================== */
  const handleCreate = async () => {
    try {
      const values = await form.validateFields();
      await createCollaboration(values);
      message.success('Tạo collaboration thành công');
      setOpen(false);
      form.resetFields();
      fetchData();
    } catch {
      message.error('Tạo thất bại');
    }
  };

  /* ===================== DELETE ===================== */
  const handleDelete = async (id) => {
    try {
      await deleteCollaboration(id);
      message.success('Đã xoá');
      fetchData();
    } catch {
      message.error('Xoá thất bại');
    }
  };

  /* ===================== TABLE ===================== */
  const columns = [
    {
      title: 'Tên',
      dataIndex: 'name',
    },
    {
      title: 'Mô tả',
      dataIndex: 'description',
    },
    {
      title: 'Hành động',
      render: (_, record) => (
        <Space>
          <Popconfirm
            title="Xoá collaboration?"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2>🤝 Collaboration Service</h2>

      <Space style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => setOpen(true)}
        >
          Tạo mới
        </Button>

        <Button icon={<ReloadOutlined />} onClick={fetchData}>
          Reload
        </Button>
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={data}
      />

      {/* ===================== MODAL CREATE ===================== */}
      <Modal
        open={open}
        title="Tạo Collaboration"
        onCancel={() => setOpen(false)}
        onOk={handleCreate}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="Tên"
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>

          <Form.Item name="description" label="Mô tả">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default CollaborationPage;
