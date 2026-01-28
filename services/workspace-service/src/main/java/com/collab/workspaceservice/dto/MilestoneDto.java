package com.collab.workspaceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime; 

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneDto {
    private String title;       
    private String description; 
    private LocalDateTime startDate; 
    private LocalDateTime endDate;   
}