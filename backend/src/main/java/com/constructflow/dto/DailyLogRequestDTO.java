package com.constructflow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class DailyLogRequestDTO {
    @NotNull
    private UUID taskId;

    @NotBlank(message = "Log entry is required")
    private String logEntry;
}
