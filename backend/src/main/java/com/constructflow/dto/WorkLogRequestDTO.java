package com.constructflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class WorkLogRequestDTO {
    @NotNull
    private UUID taskId;
    @NotNull
    private Double hours;
    private String notes;
    private LocalDate date;
    private String submittedBy;
}
