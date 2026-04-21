package com.constructflow.service.template.export;

public record ExportResult(String filename, String contentType, byte[] bytes) {}
