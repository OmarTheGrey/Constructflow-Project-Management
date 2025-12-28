package com.constructflow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TaskRequestDTO {
    private String name;
    private UUID projectId;

    private String assignee;
    private LocalDate dueDate;
    private String status;
    private String priority;
    private String description;

    @PositiveOrZero
    private BigDecimal actualCost;

    private java.util.List<String> dependencies;
}
