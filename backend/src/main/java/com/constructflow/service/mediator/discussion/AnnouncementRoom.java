package com.constructflow.service.mediator.discussion;

import com.constructflow.dto.CommentRequestDTO;
import com.constructflow.dto.CommentResponseDTO;
import com.constructflow.exception.ResourceNotFoundException;
import com.constructflow.model.Announcement;
import com.constructflow.model.AnnouncementComment;
import com.constructflow.repository.AnnouncementCommentRepository;
import com.constructflow.repository.AnnouncementRepository;
import com.constructflow.service.mapping.AnnouncementMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concrete Mediator — one logical instance managing every announcement discussion.
 * Participants register themselves at startup (default members of every room) or
 * dynamically via {@link #join(UUID, Participant)} and {@link #leave(UUID, Participant)}.
 *
 * Posting a comment is the only multi-step interaction: persist -> forward to every
 * participant of that room. Participants never call one another.
 */
@Component
@RequiredArgsConstructor
public class AnnouncementRoom implements DiscussionRoomMediator {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementCommentRepository commentRepository;
    private final AnnouncementMapper announcementMapper;

    private final AuthorParticipant authorParticipant;
    private final DashboardParticipant dashboardParticipant;
    private final ActivityRelayParticipant activityRelayParticipant;

    /** Default participants added to every room at creation time. */
    private final List<Participant> defaultParticipants = new ArrayList<>();

    /** Per-announcement dynamic participants (mentions, subscribers, etc.). */
    private final Map<UUID, List<Participant>> dynamicParticipants = new ConcurrentHashMap<>();

    @PostConstruct
    public void registerDefaults() {
        defaultParticipants.add(authorParticipant);
        defaultParticipants.add(dashboardParticipant);
        defaultParticipants.add(activityRelayParticipant);
    }

    @Override
    @Transactional
    public CommentResponseDTO post(UUID announcementId, CommentRequestDTO dto) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));

        AnnouncementComment comment = new AnnouncementComment();
        comment.setAnnouncement(announcement);
        comment.setAuthor(dto.getAuthor());
        comment.setContent(dto.getContent());
        AnnouncementComment saved = commentRepository.save(comment);

        for (Participant p : defaultParticipants) {
            safeDispatch(p, saved);
        }
        for (Participant p : dynamicParticipants.getOrDefault(announcementId, List.of())) {
            safeDispatch(p, saved);
        }

        return announcementMapper.toCommentResponse(saved);
    }

    @Override
    public void join(UUID announcementId, Participant participant) {
        dynamicParticipants
                .computeIfAbsent(announcementId, k -> new ArrayList<>())
                .add(participant);
    }

    @Override
    public void leave(UUID announcementId, Participant participant) {
        List<Participant> list = dynamicParticipants.get(announcementId);
        if (list != null) list.remove(participant);
    }

    private void safeDispatch(Participant p, AnnouncementComment comment) {
        try {
            p.onCommentPosted(comment);
        } catch (RuntimeException ignored) {
            // A misbehaving participant must not break the rest of the room.
        }
    }
}
