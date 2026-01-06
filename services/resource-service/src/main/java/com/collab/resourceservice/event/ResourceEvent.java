package com.collab.resourceservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceEvent {

    private String action;      // UPLOAD | DELETE
    private Long resourceId;
    private String fileName;
    private String uploadedBy;
}
