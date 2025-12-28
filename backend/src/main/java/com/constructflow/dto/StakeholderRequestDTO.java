package com.constructflow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class StakeholderRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    private String role;
    private String company;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    @NotNull
    private UUID projectId;
}
