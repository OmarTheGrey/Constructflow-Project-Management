package com.constructflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "daily_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DailyReport extends BaseEntity {

    @Column(name = "project_id")
    private UUID projectId;

    @Column(columnDefinition = "TEXT")
    private String activities;

    @Column(columnDefinition = "TEXT")
    private String issues;

    @ElementCollection
    private List<String> photos;

    private Double completionPercentage;
    private String submittedBy;
}
