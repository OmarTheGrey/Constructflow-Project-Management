package com.constructflow.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ResourceResponseDTO {
    private UUID id;
    private String name;
    private String category;
    private Double quantity;
    private String unit;
    private Double allocationPercentage;
    private Double cost;
    private UUID projectId;
}
