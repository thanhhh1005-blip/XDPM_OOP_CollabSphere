package com.collab.projectservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "syllabuses")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor 
@Builder
public class Syllabus {
    @Id
    private String id;

    @Column(name = "subject_id")
    private String subjectId; //

    @Column(columnDefinition = "TEXT")
    private String content; //

    private String version;
}