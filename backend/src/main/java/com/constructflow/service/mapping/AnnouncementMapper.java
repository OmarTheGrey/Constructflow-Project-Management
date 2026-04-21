package com.constructflow.service.mapping;

import com.constructflow.dto.AnnouncementResponseDTO;
import com.constructflow.dto.CommentResponseDTO;
import com.constructflow.model.Announcement;
import com.constructflow.model.AnnouncementComment;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementMapper {
    public AnnouncementResponseDTO toResponse(Announcement entity) {
        AnnouncementResponseDTO dto = new AnnouncementResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setPriority(entity.getPriority());
        dto.setDatePosted(entity.getDatePosted());
        return dto;
    }

    public CommentResponseDTO toCommentResponse(AnnouncementComment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setAuthor(comment.getAuthor());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
