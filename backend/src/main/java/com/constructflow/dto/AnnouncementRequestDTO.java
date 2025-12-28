package com.constructflow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AnnouncementRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String priority;
    private String author;
}
