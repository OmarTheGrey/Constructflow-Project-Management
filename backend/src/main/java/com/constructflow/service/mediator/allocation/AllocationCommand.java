package com.constructflow.service.mediator.allocation;

import java.util.UUID;

public record AllocationCommand(UUID taskId, UUID resourceId, double quantity) {}
