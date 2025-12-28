package com.constructflow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Document extends BaseEntity {

    private String name;
    private String type;
    private String folder;
    private LocalDateTime uploadDate;
    private String size;

    @Column(name = "project_id")
    private UUID projectId;
}
