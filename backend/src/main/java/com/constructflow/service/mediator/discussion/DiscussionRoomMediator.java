package com.constructflow.service.mediator.discussion;

import com.constructflow.dto.CommentRequestDTO;
import com.constructflow.dto.CommentResponseDTO;

import java.util.UUID;

public interface DiscussionRoomMediator {
    CommentResponseDTO post(UUID announcementId, CommentRequestDTO dto);
    void join(UUID announcementId, Participant participant);
    void leave(UUID announcementId, Participant participant);
}
