package com.constructflow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.util.UUID;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Resource extends BaseEntity {

    private String name;
    private String category;
    private Double quantity;
    private String unit;
    private Double allocationPercentage;
    private Double cost;
    private UUID projectId; // Link to Project
}
