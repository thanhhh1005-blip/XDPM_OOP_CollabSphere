package com.collab.projectservice.events;

import lombok.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEvent implements Serializable {
    private String projectId;
    private String title;
    private String status;
    private String message;
}