package com.constructflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.EqualsAndHashCode;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Task extends BaseEntity {

    private String name;

    @Column(name = "project_id")
    private UUID projectId;

    private String assignee;
    private LocalDate dueDate;
    private String status;
    private String priority;
    private String description;

    @Column(precision = 15, scale = 2)
    private BigDecimal actualCost;

    @ElementCollection
    private java.util.List<String> dependencies;
}
