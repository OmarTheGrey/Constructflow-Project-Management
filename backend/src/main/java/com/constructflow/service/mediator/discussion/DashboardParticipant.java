package com.constructflow.service.mediator.discussion;

import com.constructflow.model.AnnouncementComment;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Participant that maintains a per-announcement comment counter so the
 * dashboard can poll it without re-running a COUNT query.
 */
@Component
public class DashboardParticipant implements Participant {

    private final ConcurrentHashMap<UUID, AtomicLong> countsByAnnouncement = new ConcurrentHashMap<>();

    @Override
    public void onCommentPosted(AnnouncementComment comment) {
        UUID announcementId = comment.getAnnouncement().getId();
        countsByAnnouncement.computeIfAbsent(announcementId, k -> new AtomicLong()).incrementAndGet();
    }

    public long commentsOn(UUID announcementId) {
        AtomicLong c = countsByAnnouncement.get(announcementId);
        return c == null ? 0L : c.get();
    }

    @Override public String name() { return "dashboard"; }
}
