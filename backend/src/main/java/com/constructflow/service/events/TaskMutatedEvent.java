package com.constructflow.service.events;

import java.util.UUID;

public record TaskMutatedEvent(UUID projectId) {}
