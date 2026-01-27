// package com.collab.notificationservice.service;

// import com.collab.notificationservice.entity.Notification;
// import lombok.RequiredArgsConstructor;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Service;

// @Service
// @RequiredArgsConstructor
// public class WebSocketPushService {

//     private final SimpMessagingTemplate messagingTemplate;

//     public void push(Notification notification) {
//         messagingTemplate.convertAndSend(
//                 "/topic/notifications/" + notification.getUserId(),
//                 notification
//         );
//     }
// }
