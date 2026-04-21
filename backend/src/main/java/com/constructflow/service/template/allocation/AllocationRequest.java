package com.constructflow.service.template.allocation;

import java.util.UUID;

public record AllocationRequest(UUID taskId, UUID resourceId, double quantity) {}
