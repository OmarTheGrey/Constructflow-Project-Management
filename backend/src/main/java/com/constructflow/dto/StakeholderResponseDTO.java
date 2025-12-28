package com.constructflow.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class StakeholderResponseDTO {
    private UUID id;
    private String name;
    private String role;
    private String company;
    private String email;
    private String phone;
    private UUID projectId;
}
