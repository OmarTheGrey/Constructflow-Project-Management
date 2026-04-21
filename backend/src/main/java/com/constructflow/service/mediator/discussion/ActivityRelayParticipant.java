package com.constructflow.service.mediator.discussion;

import com.constructflow.model.AnnouncementComment;
import com.constructflow.service.observer.Activity;
import com.constructflow.service.observer.ActivityHub;
import org.springframework.stereotype.Component;

/**
 * Participant that relays every comment posted in the room onto the global
 * ActivityHub, so anything subscribed to the hub (audit log, counters, alerts)
 * picks it up without the mediator needing to know about them.
 */
@Component
public class ActivityRelayParticipant implements Participant {

    @Override
    public void onCommentPosted(AnnouncementComment comment) {
        ActivityHub.INSTANCE.publish(new Activity.CommentPosted(
                comment.getAnnouncement().getId(),
                comment.getId(),
                comment.getAuthor()));
    }

    @Override public String name() { return "activity-relay"; }
}
