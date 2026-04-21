package com.constructflow.service.mediator.allocation;

import com.constructflow.service.observer.Activity;
import com.constructflow.service.observer.ActivityHub;
import org.springframework.stereotype.Component;

/** Colleague — broadcasts a ResourceAllocated activity through the global hub. */
@Component
public class NotificationColleague {

    public void broadcast(AllocationCommand command) {
        ActivityHub.INSTANCE.publish(
                new Activity.ResourceAllocated(command.taskId(), command.resourceId(), command.quantity()));
    }
}
