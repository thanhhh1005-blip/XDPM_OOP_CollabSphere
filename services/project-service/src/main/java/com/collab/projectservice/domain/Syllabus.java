package com.collab.projectservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "syllabuses")
@Data
public class Syllabus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT") // Để lưu văn bản dài
    private String content; // Nội dung chính của đề cương (cái mà bạn nhập vào AI)

    private String objectives; // Mục tiêu môn học (tùy chọn)

    // Liên kết ngược về Project (OneToOne)
    @OneToOne(mappedBy = "syllabus")
    @JsonIgnore
    @ToString.Exclude
    private Project project;
}