import React, { useState, useEffect, useRef } from 'react';
import { Tabs, Card } from 'antd';
import { Tldraw } from 'tldraw';
import ReactQuill from 'react-quill';
import 'tldraw/tldraw.css';
import 'react-quill/dist/quill.snow.css';
import { Client } from '@stomp/stompjs';

const CollabPage = () => {
    const [text, setText] = useState('');
    const stompClient = useRef(null);
    const groupId = "group_1"; // Sau này lấy từ thông tin nhóm của User

    useEffect(() => {
        // Kết nối WebSocket
        stompClient.current = new Client({
            brokerURL: 'ws://localhost:8080/ws-collab', // Qua Gateway
            onConnect: () => {
                console.log("Collab System Connected!");
                
                // Đăng ký nhận text đồng bộ
                stompClient.current.subscribe(`/topic/collab/editor/${groupId}`, (message) => {
                    if (message.body !== text) setText(message.body);
                });
            },
        });
        stompClient.current.activate();
        return () => stompClient.current.deactivate();
    }, []);

    // Xử lý khi gõ chữ
    const handleTextChange = (value) => {
        setText(value);
        if (stompClient.current && stompClient.current.connected) {
            stompClient.current.publish({
                destination: `/app/editor/${groupId}`,
                body: value
            });
        }
    };

    return (
        <div style={{ padding: '20px', height: 'calc(100vh - 150px)' }}>
            <Tabs defaultActiveKey="1" items={[
                {
                    key: '1',
                    label: '🎨 Bảng trắng (Whiteboard)',
                    children: (
                        <div style={{ height: '70vh', border: '1px solid #ddd' }}>
                            {/* Tldraw tự xử lý logic vẽ, để đồng bộ cần nâng cấp thêm, 
                                trước mắt em cứ show bảng vẽ cho xịn đã */}
                            <Tldraw />
                        </div>
                    )
                },
                {
                    key: '2',
                    label: '📝 Soạn thảo chung (Editor)',
                    children: (
                        <Card>
                            <ReactQuill 
                                theme="snow" 
                                value={text} 
                                onChange={handleTextChange} 
                                style={{ height: '50vh', marginBottom: '40px' }}
                            />
                        </Card>
                    )
                }
            ]} />
        </div>
    );
};

export default CollabPage;