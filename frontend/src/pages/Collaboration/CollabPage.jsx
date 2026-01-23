import React, { useState, useEffect, useRef } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import { Tabs, Card, Typography, Spin, Button, Space, Tag } from 'antd';
import { Tldraw } from 'tldraw';
import ReactQuill from 'react-quill-new';
import 'tldraw/tldraw.css';
import 'react-quill-new/dist/quill.snow.css';
import { Client } from '@stomp/stompjs';
import axios from 'axios';
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons';

const { Title } = Typography;

const CollabPage = () => {
    const { roomId } = useParams(); 
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const roomName = searchParams.get('name') || "Phòng cộng tác";

    const [docContent, setDocContent] = useState('');
    const [isEditorReady, setIsEditorReady] = useState(false);
    const stompClient = useRef(null);
    const isReceiving = useRef(false);
    const whiteboardEditor = useRef(null);

    // --- LOGIC 1: TẢI DỮ LIỆU CŨ ---
    useEffect(() => {
        const loadInitialData = async () => {
            try {
                const resDoc = await axios.get(`http://localhost:8080/api/collab/editor/${roomId}`);
                if (resDoc.data?.result) setDocContent(resDoc.data.result);
                setIsEditorReady(true);
            } catch (e) {}
        };
        loadInitialData();

        const client = new Client({
            brokerURL: 'ws://localhost:8080/ws-collab',
            onConnect: () => {
                console.log("✅ Collab Socket Connected");
                // Nhận tin nhắn văn bản
                client.subscribe(`/topic/collab/editor/${roomId}`, (msg) => {
                    if (!isReceiving.current) setDocContent(msg.body);
                });
                // Nhận dữ liệu bảng vẽ (Chỉ nhận khi người khác lưu xong)
                client.subscribe(`/topic/collab/whiteboard/${roomId}`, (msg) => {
                    if (!isReceiving.current && whiteboardEditor.current) {
                        try {
                            isReceiving.current = true;
                            whiteboardEditor.current.loadSnapshot(JSON.parse(msg.body));
                            setTimeout(() => isReceiving.current = false, 200);
                        } catch (e) {}
                    }
                });
            },
        });
        client.activate();
        stompClient.current = client;
        return () => client.deactivate();
    }, [roomId]);

    // --- LOGIC 2: EDITOR (Gõ chữ) ---
    const handleTextChange = (value, delta, source) => {
        if (source === 'user') {
            isReceiving.current = true;
            setDocContent(value);
            if (stompClient.current?.connected) {
                stompClient.current.publish({
                    destination: `/app/editor/${roomId}`,
                    body: value
                });
            }
            setTimeout(() => isReceiving.current = false, 100);
        }
    };

    // --- LOGIC 3: WHITEBOARD (Fix LAG bằng cách Debounce) ---
    const handleBoardMount = (editorInstance) => {
        whiteboardEditor.current = editorInstance;
        
        // Tải bảng cũ
        axios.get(`http://localhost:8080/api/collab/whiteboard/${roomId}`).then(res => {
            if (res.data?.result && res.data.result !== "{}") {
                try { editorInstance.loadSnapshot(JSON.parse(res.data.result)); } catch (e) {}
            }
        });

        // Chỉ gửi dữ liệu vẽ sau khi người dùng ngừng thao tác 1 giây để tránh lag
        let timeout;
        editorInstance.store.listen((entry) => {
            if (entry.source === 'user' && !isReceiving.current) {
                clearTimeout(timeout);
                timeout = setTimeout(() => {
                    if (stompClient.current?.connected) {
                        const snapshot = JSON.stringify(editorInstance.getSnapshot());
                        stompClient.current.publish({
                            destination: `/app/whiteboard/${roomId}`,
                            body: snapshot
                        });
                    }
                }, 1000); // 1 giây mới sync 1 lần -> Hết lag ngay
            }
        });
    };

    return (
        <div style={{ padding: '15px', height: 'calc(100vh - 100px)', display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
            {/* Header có nút Back */}
            <div style={{ marginBottom: 15, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Space>
                    <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/collaboration')}>Quay lại</Button>
                    <Title level={4} style={{ margin: 0 }}>🚀 {roomName}</Title>
                </Space>
                <Tag color="green">Chế độ đồng bộ: Realtime</Tag>
            </div>

            <Tabs 
                defaultActiveKey="1" 
                type="card"
                style={{ flex: 1, display: 'flex', flexDirection: 'column' }}
                // Sửa UI bị che: Thêm overflow hidden cho Tabs
                className="collab-tabs"
                items={[
                    {
                        key: '1',
                        label: '📝 Soạn thảo chung',
                        children: (
                            <div style={{ height: 'calc(100vh - 250px)', overflow: 'auto' }}>
                                {isEditorReady ? (
                                    <ReactQuill theme="snow" value={docContent} onChange={handleTextChange} style={{ height: '90%' }} />
                                ) : <Spin />}
                            </div>
                        )
                    },
                    {
                        key: '2',
                        label: '🎨 Bảng vẽ nhóm',
                        children: (
                            <div style={{ height: 'calc(100vh - 250px)', border: '1px solid #ddd', position: 'relative' }}>
                                <Tldraw onMount={handleBoardMount} />
                            </div>
                        )
                    }
                ]} 
            />
        </div>
    );
};

export default CollabPage;