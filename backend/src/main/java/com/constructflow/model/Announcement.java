package com.constructflow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Announcement extends BaseEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String priority;
    private LocalDateTime datePosted;
    private String author;
}
