package com.constructflow.service.template.export;

import java.util.UUID;

public record ExportRequest(UUID documentId, ExportFormat format, String requestedBy) {}
