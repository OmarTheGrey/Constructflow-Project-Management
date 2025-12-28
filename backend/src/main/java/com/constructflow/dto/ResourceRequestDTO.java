package com.constructflow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResourceRequestDTO {
    @NotBlank(message = "Resource name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull
    @PositiveOrZero
    private Double quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @PositiveOrZero
    @Max(100)
    private Double allocationPercentage;

    @PositiveOrZero
    private Double cost; // Added cost field

    private java.util.UUID projectId; // Added projectId
}
