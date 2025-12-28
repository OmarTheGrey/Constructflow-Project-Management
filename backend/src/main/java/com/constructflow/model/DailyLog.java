package com.constructflow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DailyLog extends BaseEntity {

    @Column(name = "task_id")
    private UUID taskId;

    @Column(columnDefinition = "TEXT")
    private String logEntry;

    private LocalDateTime dateCreated;
}
