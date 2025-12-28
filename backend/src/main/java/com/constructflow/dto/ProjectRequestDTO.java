package com.constructflow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectRequestDTO {
    @Size(min = 3, max = 100)
    private String name;

    private String client;
    private String location;

    @PositiveOrZero
    private BigDecimal budget;

    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String objectives;
    private java.util.List<String> milestones;
}
