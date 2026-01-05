package com.collab.collaborationservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResourceResponse {

    private Long resourceId;
    private String fileName;
    private String fileType;

    private Long sharedBy;
    private LocalDateTime sharedAt;
}
