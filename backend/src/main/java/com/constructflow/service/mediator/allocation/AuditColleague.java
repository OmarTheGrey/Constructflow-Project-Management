package com.constructflow.service.mediator.allocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Colleague — writes an audit line for every allocation orchestrated by the mediator. */
@Component
public class AuditColleague {

    private static final Logger log = LoggerFactory.getLogger("AUDIT");

    public void recordAllocation(AllocationCommand command, String resourceName) {
        log.info("ALLOCATION taskId={} resource='{}' ({}) quantity={}",
                command.taskId(), resourceName, command.resourceId(), command.quantity());
    }
}
