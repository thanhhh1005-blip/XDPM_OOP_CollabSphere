package com.collab.workspaceservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "workspaces")
@Data
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = true, unique = true) 
    private String teamId;// Đây là "Chìa khóa" duy nhất để liên kết
    @Column(name = "class_id", nullable = false) 
    private Long classId;
    // Các thiết lập riêng của workspace (nếu cần)
    private String settingConfig; 

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Sprint> sprints;
}