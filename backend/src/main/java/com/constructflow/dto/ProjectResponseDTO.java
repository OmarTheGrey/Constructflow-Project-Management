package com.constructflow.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProjectResponseDTO {
    private UUID id;
    private String name;
    private String client;
    private String location;
    private BigDecimal budget;
    private BigDecimal actualCost;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double progress;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private String objectives;
    private java.util.List<String> milestones;
}
