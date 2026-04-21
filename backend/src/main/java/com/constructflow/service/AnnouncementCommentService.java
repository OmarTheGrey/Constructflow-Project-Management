package com.constructflow.service;

import com.constructflow.dto.CommentRequestDTO;
import com.constructflow.dto.CommentResponseDTO;
import com.constructflow.repository.AnnouncementCommentRepository;
import com.constructflow.service.mapping.AnnouncementMapper;
import com.constructflow.service.mediator.discussion.DiscussionRoomMediator;
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
    private final AnnouncementMapper announcementMapper;
    private final DiscussionRoomMediator discussionRoomMediator;

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByAnnouncement(UUID announcementId) {
        return commentRepository.findByAnnouncementIdOrderByCreatedAtAsc(announcementId).stream()
                .map(announcementMapper::toCommentResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDTO addComment(UUID announcementId, CommentRequestDTO dto) {
        return discussionRoomMediator.post(announcementId, dto);
    }

    @Transactional
    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }
}
