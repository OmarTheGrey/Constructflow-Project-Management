package com.constructflow.service.observer;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Sealed hierarchy of activities that can be broadcast through the ActivityHub.
 * Each variant is an immutable record — safe to fan out across observers.
 */
public sealed interface Activity {

    LocalDateTime at();

    record TaskCreated(UUID taskId, UUID projectId, String name, LocalDateTime at) implements Activity {
        public TaskCreated(UUID taskId, UUID projectId, String name) {
            this(taskId, projectId, name, LocalDateTime.now());
        }
    }

    record TaskCompleted(UUID taskId, UUID projectId, String name, LocalDateTime at) implements Activity {
        public TaskCompleted(UUID taskId, UUID projectId, String name) {
            this(taskId, projectId, name, LocalDateTime.now());
        }
    }

    record TaskOverdue(UUID taskId, String name, LocalDateTime at) implements Activity {
        public TaskOverdue(UUID taskId, String name) {
            this(taskId, name, LocalDateTime.now());
        }
    }

    record ResourceAllocated(UUID taskId, UUID resourceId, double quantity, LocalDateTime at) implements Activity {
        public ResourceAllocated(UUID taskId, UUID resourceId, double quantity) {
            this(taskId, resourceId, quantity, LocalDateTime.now());
        }
    }

    record CommentPosted(UUID announcementId, UUID commentId, String author, LocalDateTime at) implements Activity {
        public CommentPosted(UUID announcementId, UUID commentId, String author) {
            this(announcementId, commentId, author, LocalDateTime.now());
        }
    }
}
