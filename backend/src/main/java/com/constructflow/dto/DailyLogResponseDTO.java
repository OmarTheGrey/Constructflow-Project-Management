package com.constructflow.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DailyLogResponseDTO {
    private UUID id;
    private UUID taskId;
    private String logEntry;
    private LocalDateTime dateCreated;
}
