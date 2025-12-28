package com.constructflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "work_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkLog extends BaseEntity {

    @Column(name = "task_id")
    private UUID taskId;

    private Double hours;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDate date;
    private String submittedBy;
}
