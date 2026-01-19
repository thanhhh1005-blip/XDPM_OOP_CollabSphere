package com.collab.projectservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_seq")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProjectSeq {
  @Id
  private Integer id;

  @Column(name = "next_val", nullable = false)
  private Long nextVal;
}
