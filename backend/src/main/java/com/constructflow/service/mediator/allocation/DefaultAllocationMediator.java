package com.constructflow.service.mediator.allocation;

import com.constructflow.model.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Concrete Mediator — sequences the multi-step allocation workflow so colleagues
 * never call one another directly. The fixed order here is the business rule:
 *
 *   reserve(stock) -> record(allocation row) -> broadcast(event) -> audit(log)
 *
 * If any earlier step throws, later steps are skipped and the transaction
 * rolls back.
 */
@Component
@RequiredArgsConstructor
public class DefaultAllocationMediator implements AllocationMediator {

    private final ResourceColleague resourceColleague;
    private final TaskAllocationColleague taskAllocationColleague;
    private final NotificationColleague notificationColleague;
    private final AuditColleague auditColleague;

    @Override
    @Transactional
    public void allocate(AllocationCommand command) {
        Resource resource = resourceColleague.reserve(command);
        taskAllocationColleague.record(command);
        notificationColleague.broadcast(command);
        auditColleague.recordAllocation(command, resource.getName());
    }
}
