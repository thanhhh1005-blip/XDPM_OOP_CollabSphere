package com.collab.teamservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data // Tự tạo Getter, Setter
@NoArgsConstructor // 👈 QUAN TRỌNG: Tạo constructor rỗng cho Jackson dùng
@AllArgsConstructor
public class CreateTeamReq {
    private String name;
    private Long classId;
    @JsonProperty("projectId")
    private String projectId;
    private String leaderId;
    private List<String> memberIds;
}