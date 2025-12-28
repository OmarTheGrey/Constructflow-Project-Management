package com.constructflow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.util.UUID;

@Entity
@Table(name = "stakeholders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Stakeholder extends BaseEntity {

    private String name;
    private String role;
    private String company;
    private String email;
    private String phone;

    @Column(name = "project_id")
    private UUID projectId;
}
