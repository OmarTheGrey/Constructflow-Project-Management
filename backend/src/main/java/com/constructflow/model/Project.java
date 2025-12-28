package com.constructflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Project extends BaseEntity {

    private String name;
    private String client;
    private String location;

    @Column(precision = 15, scale = 2)
    private BigDecimal budget;

    @Column(precision = 15, scale = 2)
    private BigDecimal actualCost;

    private LocalDate startDate;
    private LocalDate endDate;
    private Double progress;
    private String status;

    @Column(columnDefinition = "TEXT")
    private String objectives;

    @ElementCollection
    private java.util.List<String> milestones;
}
