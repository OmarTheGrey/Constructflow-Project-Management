package com.constructflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class DailyReportRequestDTO {
    @NotNull
    private UUID projectId;
    private String activities;
    private String issues;
    private List<String> photos;
    private Double completionPercentage;
    private String submittedBy;
}
