import React from "react";
import { JitsiMeeting } from "@jitsi/react-sdk";
import { Spin } from "antd";

const VideoCall = ({ roomId, username, password, onLeave }) => {
  const roomName = `CollabSphere_Meeting_${roomId}`; // Tên phòng duy nhất

  return (
    <div style={{ height: "600px", width: "100%" }}>
      <JitsiMeeting
        domain="meet.ffmuc.net" // Server Free của Jitsi
        roomName={roomName}
        configOverwrite={{
          startWithAudioMuted: true,
          disableThirdPartyRequests: true,
          prejoinPageEnabled: false, // Vào thẳng luôn không cần chờ
        }}
        interfaceConfigOverwrite={{
          TOOLBAR_BUTTONS: [
            "microphone",
            "camera",
            "closedcaptions",
            "desktop",
            "fullscreen",
            "fodeviceselection",
            "hangup",
            "profile",
            "chat",
            "recording",
            "livestreaming",
            "etherpad",
            "sharedvideo",
            "settings",
            "raisehand",
            "videoquality",
            "filmstrip",
            "invite",
            "feedback",
            "stats",
            "shortcuts",
            "tileview",
            "videobackgroundblur",
            "download",
            "help",
            "mute-everyone",
            "security",
          ],
        }}
        userInfo={{
          displayName: username,
        }}
        onApiReady={(externalApi) => {
          // 1. Khi một người tham gia cuộc họp
          externalApi.addEventListener("videoConferenceJoined", () => {
            if (password) {
              // Tự động điền mật khẩu từ hệ thống vào Jitsi ngầm bên dưới
              externalApi.executeCommand("password", password);
              console.log("Hệ thống đã tự động xác thực mật khẩu.");
            }
          });

          // 2. Dự phòng: Nếu vai trò thay đổi thành quản trị viên (Moderator)
          externalApi.addEventListener("participantRoleChanged", (event) => {
            if (event.role === "moderator" && password) {
              externalApi.executeCommand("password", password);
            }
          });

          // 3. Lắng nghe khi user thoát
          externalApi.addEventListeners({
            videoConferenceLeft: () => onLeave(),
          });
        }}
        getIFrameRef={(iframeRef) => {
          iframeRef.style.height = "100%";
        }}
        spinner={() => <Spin size="large" tip="Đang kết nối Video..." />}
      />
    </div>
  );
};

export default VideoCall;
