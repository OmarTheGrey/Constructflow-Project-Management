package com.constructflow.service.events;

import com.constructflow.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProgressRecalculator {

    private final ProjectService projectService;

    // BEFORE_COMMIT so the inner @Transactional recalc joins the outer task-mutating
    // transaction. If recalc fails, the whole task mutation rolls back — preferable to
    // returning 201 with silently stale project progress (the AFTER_COMMIT behaviour).
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onTaskMutated(TaskMutatedEvent event) {
        if (event.projectId() != null) {
            projectService.updateProjectProgress(event.projectId());
        }
    }
}
