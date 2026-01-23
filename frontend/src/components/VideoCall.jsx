import React from "react";
import { JitsiMeeting } from "@jitsi/react-sdk";
import { Spin } from "antd";

const VideoCall = ({ roomId, username, password, onLeave }) => {
  // roomId lúc này sẽ có dạng "TEAM_10" hoặc "CLASS_5"
  const roomName = `CollabSphere_Room_${roomId}`; 

  return (
    <div style={{ height: "600px", width: "100%" }}>
      <JitsiMeeting
        domain="meet.ffmuc.net" 
        roomName={roomName}
        configOverwrite={{
          startWithAudioMuted: true,
          disableThirdPartyRequests: true,
          prejoinPageEnabled: false,
        }}
        userInfo={{ displayName: username }}
        onApiReady={(externalApi) => {
          // Tự động điền pass nếu có
          externalApi.addEventListener("videoConferenceJoined", () => {
            if (password) externalApi.executeCommand("password", password);
          });
          externalApi.addEventListeners({
            videoConferenceLeft: () => onLeave(),
          });
        }}
        getIFrameRef={(iframeRef) => { iframeRef.style.height = "100%"; }}
        spinner={() => <Spin size="large" tip="Đang kết nối Video..." />}
      />
    </div>
  );
};
export default VideoCall;