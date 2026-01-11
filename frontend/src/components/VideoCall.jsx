import React from 'react';
import { JitsiMeeting } from '@jitsi/react-sdk';
import { Spin } from 'antd';

const VideoCall = ({ roomId, username, onLeave }) => {
    const roomName = `CollabSphere_Meeting_${roomId}`; // Tên phòng duy nhất

    return (
        <div style={{ height: '600px', width: '100%' }}>
            <JitsiMeeting
                domain="meet.jit.si" // Server Free của Jitsi
                roomName={roomName}
                configOverwrite={{
                    startWithAudioMuted: true,
                    disableThirdPartyRequests: true,
                    prejoinPageEnabled: false, // Vào thẳng luôn không cần chờ
                }}
                interfaceConfigOverwrite={{
                    TOOLBAR_BUTTONS: [
                        'microphone', 'camera', 'closedcaptions', 'desktop', 'fullscreen',
                        'fodeviceselection', 'hangup', 'profile', 'chat', 'recording',
                        'livestreaming', 'etherpad', 'sharedvideo', 'settings', 'raisehand',
                        'videoquality', 'filmstrip', 'invite', 'feedback', 'stats', 'shortcuts',
                        'tileview', 'videobackgroundblur', 'download', 'help', 'mute-everyone', 'security'
                    ],
                }}
                userInfo={{
                    displayName: username
                }}
                onApiReady={(externalApi) => {
                    // Xử lý khi user bấm nút tắt máy (Hangup)
                    externalApi.addEventListeners({
                        videoConferenceLeft: () => onLeave()
                    });
                }}
                getIFrameRef={(iframeRef) => { iframeRef.style.height = '100%'; }}
                spinner={() => <Spin size="large" tip="Đang kết nối Video..." />}
            />
        </div>
    );
};

export default VideoCall;