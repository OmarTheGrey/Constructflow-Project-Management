package com.constructflow.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WorkLogResponseDTO {
    private UUID id;
    private UUID taskId;
    private Double hours;
    private String notes;
    private LocalDate date;
    private String submittedBy;
    private LocalDateTime createdAt;
}
