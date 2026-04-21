package com.constructflow.service;

import com.constructflow.dto.CommentRequestDTO;
import com.constructflow.dto.CommentResponseDTO;
import com.constructflow.exception.ResourceNotFoundException;
import com.constructflow.model.Announcement;
import com.constructflow.model.AnnouncementComment;
import com.constructflow.repository.AnnouncementCommentRepository;
import com.constructflow.repository.AnnouncementRepository;
import com.constructflow.service.mapping.AnnouncementMapper;
import com.constructflow.service.observer.Activity;
import com.constructflow.service.observer.ActivityHub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementCommentService {
    private final AnnouncementCommentRepository commentRepository;
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementMapper announcementMapper;

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByAnnouncement(UUID announcementId) {
        return commentRepository.findByAnnouncementIdOrderByCreatedAtAsc(announcementId).stream()
                .map(announcementMapper::toCommentResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDTO addComment(UUID announcementId, CommentRequestDTO dto) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));
        AnnouncementComment comment = new AnnouncementComment();
        comment.setAnnouncement(announcement);
        comment.setAuthor(dto.getAuthor());
        comment.setContent(dto.getContent());
        AnnouncementComment saved = commentRepository.save(comment);
        ActivityHub.INSTANCE.publish(
                new Activity.CommentPosted(announcementId, saved.getId(), saved.getAuthor()));
        return announcementMapper.toCommentResponse(saved);
    }

    @Transactional
    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }
}
