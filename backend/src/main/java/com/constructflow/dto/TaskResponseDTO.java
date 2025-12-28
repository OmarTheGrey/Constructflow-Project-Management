package com.constructflow.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskResponseDTO {
    private UUID id;
    private String name;
    private UUID projectId;
    private String assignee;
    private LocalDate dueDate;
    private String status;
    private String priority;
    private String description;
    private BigDecimal actualCost;
    private LocalDateTime createdAt;
    private java.util.List<String> dependencies;
}
