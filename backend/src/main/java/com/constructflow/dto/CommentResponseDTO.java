package com.constructflow.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommentResponseDTO {
    private UUID id;
    private String author;
    private String content;
    private LocalDateTime createdAt;
}
