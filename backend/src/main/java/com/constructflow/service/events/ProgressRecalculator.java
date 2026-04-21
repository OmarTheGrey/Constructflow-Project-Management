package com.constructflow.service.events;

import com.constructflow.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProgressRecalculator {

    private final ProjectService projectService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTaskMutated(TaskMutatedEvent event) {
        if (event.projectId() != null) {
            projectService.updateProjectProgress(event.projectId());
        }
    }
}
