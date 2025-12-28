package com.constructflow.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DocumentResponseDTO {
    private UUID id;
    private String name;
    private String type;
    private String folder;
    private LocalDateTime uploadDate;
    private String size;
}
