package com.constructflow.service.observer;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Writes every activity to the application log as an append-only audit trail.
 * Registers itself with the {@link ActivityHub} singleton at startup.
 */
@Component
public class AuditLogObserver implements ActivityObserver {

    private static final Logger log = LoggerFactory.getLogger("AUDIT");

    @PostConstruct
    public void register() {
        ActivityHub.INSTANCE.subscribe(this);
    }

    @Override
    public void onActivity(Activity activity) {
        log.info("[{}] {}", activity.at(), activity);
    }
}
