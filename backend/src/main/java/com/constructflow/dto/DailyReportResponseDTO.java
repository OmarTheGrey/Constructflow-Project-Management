package com.constructflow.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class DailyReportResponseDTO {
    private UUID id;
    private UUID projectId;
    private String activities;
    private String issues;
    private List<String> photos;
    private Double completionPercentage;
    private String submittedBy;
    private LocalDateTime createdAt;
}
