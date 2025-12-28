package com.constructflow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class DocumentRequestDTO {
    @NotBlank(message = "Document name is required")
    private String name;

    @NotBlank(message = "Type is required")
    private String type;

    private String folder;

    @NotNull
    private UUID projectId;
}
