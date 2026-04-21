package com.constructflow.service.observer;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Reacts only to {@link Activity.TaskOverdue} events and surfaces them as
 * high-visibility WARN log lines. In a real deployment this would dispatch
 * to Slack / email — the shape stays the same.
 */
@Component
public class OverdueAlertObserver implements ActivityObserver {

    private static final Logger log = LoggerFactory.getLogger("ALERTS");

    @PostConstruct
    public void register() {
        ActivityHub.INSTANCE.subscribe(this);
    }

    @Override
    public void onActivity(Activity activity) {
        if (activity instanceof Activity.TaskOverdue overdue) {
            log.warn("OVERDUE task id={} name='{}' (detected at {})",
                    overdue.taskId(), overdue.name(), overdue.at());
        }
    }
}
