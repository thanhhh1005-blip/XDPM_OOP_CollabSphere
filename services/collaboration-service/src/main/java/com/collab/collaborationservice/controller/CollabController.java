package com.collab.collaborationservice.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CollabController {

    private final SimpMessagingTemplate messagingTemplate;

    public CollabController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 1. Đồng bộ nét vẽ Whiteboard theo Group ID
    @MessageMapping("/whiteboard/{groupId}")
    public void syncDrawing(@DestinationVariable String groupId, @Payload String drawData) {
        messagingTemplate.convertAndSend("/topic/collab/whiteboard/" + groupId, drawData);
    }

    // 2. Đồng bộ văn bản Text Editor theo Group ID
    @MessageMapping("/editor/{groupId}")
    public void syncText(@DestinationVariable String groupId, @Payload String textData) {
        messagingTemplate.convertAndSend("/topic/collab/editor/" + groupId, textData);
    }
}