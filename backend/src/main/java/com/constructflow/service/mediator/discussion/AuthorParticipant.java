package com.constructflow.service.mediator.discussion;

import com.constructflow.model.AnnouncementComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Participant that represents the author of the original announcement. In a
 * real system this would deliver a push notification — here it logs at INFO.
 */
@Component
public class AuthorParticipant implements Participant {

    private static final Logger log = LoggerFactory.getLogger("NOTIFICATION");

    @Override
    public void onCommentPosted(AnnouncementComment comment) {
        log.info("author-notification: new comment on announcement id={} by {}",
                comment.getAnnouncement().getId(), comment.getAuthor());
    }

    @Override public String name() { return "author"; }
}
