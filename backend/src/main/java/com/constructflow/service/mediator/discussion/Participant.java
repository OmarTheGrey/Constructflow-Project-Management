package com.constructflow.service.mediator.discussion;

import com.constructflow.model.AnnouncementComment;

/**
 * Colleague interface — participants receive notifications through the mediator;
 * they never talk to each other directly.
 */
public interface Participant {
    void onCommentPosted(AnnouncementComment comment);
    String name();
}
