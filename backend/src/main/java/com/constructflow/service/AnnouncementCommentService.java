package com.constructflow.service;

import com.constructflow.dto.CommentRequestDTO;
import com.constructflow.dto.CommentResponseDTO;
import com.constructflow.model.Announcement;
import com.constructflow.model.AnnouncementComment;
import com.constructflow.repository.AnnouncementCommentRepository;
import com.constructflow.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnnouncementCommentService {

    @Autowired
    private AnnouncementCommentRepository commentRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByAnnouncement(UUID announcementId) {
        return commentRepository.findByAnnouncementIdOrderByCreatedAtAsc(announcementId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDTO addComment(UUID announcementId, CommentRequestDTO dto) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        AnnouncementComment comment = new AnnouncementComment();
        comment.setAnnouncement(announcement);
        comment.setAuthor(dto.getAuthor());
        comment.setContent(dto.getContent());

        AnnouncementComment savedComment = commentRepository.save(comment);
        return mapToResponseDTO(savedComment);
    }

    @Transactional
    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    private CommentResponseDTO mapToResponseDTO(AnnouncementComment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setAuthor(comment.getAuthor());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
