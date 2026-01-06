package com.collab.collaborationservice.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollaborationCreatedEvent {
    private Long collaborationId;
    private String name;
    private String createdBy;
    private LocalDateTime createdAt;
}
