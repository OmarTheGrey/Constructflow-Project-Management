package com.constructflow.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AnnouncementResponseDTO {
    private UUID id;
    private String title;
    private String content;
    private String priority;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datePosted;
}
