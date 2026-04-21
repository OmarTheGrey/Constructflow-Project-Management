package com.constructflow.service.mediator.allocation;

import com.constructflow.model.TaskAllocation;
import com.constructflow.repository.TaskAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** Colleague — owns the TaskAllocation row persistence. */
@Component
@RequiredArgsConstructor
public class TaskAllocationColleague {

    private final TaskAllocationRepository taskAllocationRepository;

    public TaskAllocation record(AllocationCommand command) {
        TaskAllocation allocation = new TaskAllocation();
        allocation.setTaskId(command.taskId());
        allocation.setResourceId(command.resourceId());
        allocation.setQuantityAllocated(command.quantity());
        allocation.setAllocatedAt(LocalDateTime.now());
        return taskAllocationRepository.save(allocation);
    }
}
