package com.constructflow.service.template.export;

import com.constructflow.exception.ResourceNotFoundException;
import com.constructflow.model.Document;
import com.constructflow.repository.DocumentRepository;
import com.constructflow.service.storage.DocumentStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractDocumentExporter {

    protected static final Logger log = LoggerFactory.getLogger(AbstractDocumentExporter.class);

    protected final DocumentRepository documentRepository;
    protected final DocumentStorage documentStorage;

    protected AbstractDocumentExporter(DocumentRepository documentRepository,
                                       DocumentStorage documentStorage) {
        this.documentRepository = documentRepository;
        this.documentStorage = documentStorage;
    }

    /**
     * Template method — fixed algorithm, cannot be overridden.
     */
    public final ExportResult export(ExportRequest request) throws IOException {
        validateAccess(request);
        Document document = resolveDocument(request);
        byte[] rawContent = loadContent(document);
        byte[] transformed = transform(document, rawContent);
        String filename = buildFilename(document);
        logExport(request, filename);
        return new ExportResult(filename, contentType(), transformed);
    }

    // Hook: default impl — subclasses may override.
    protected void validateAccess(ExportRequest request) {
        if (request.requestedBy() == null || request.requestedBy().isBlank()) {
            throw new IllegalArgumentException("Export requester must be provided");
        }
    }

    // Common concrete step — shared by all subclasses.
    protected Document resolveDocument(ExportRequest request) {
        return documentRepository.findById(request.documentId())
                .orElseThrow(() -> new ResourceNotFoundException("Document", request.documentId()));
    }

    // Common concrete step.
    protected byte[] loadContent(Document document) throws IOException {
        if (document.getStorageKey() == null) {
            return new byte[0];
        }
        try (InputStream in = documentStorage.load(document.getStorageKey())) {
            return in.readAllBytes();
        }
    }

    // Abstract steps — each subclass must supply its own transform and content-type.
    protected abstract byte[] transform(Document document, byte[] rawContent) throws IOException;
    protected abstract String contentType();
    protected abstract String fileExtension();

    // Default filename rule — subclasses may override.
    protected String buildFilename(Document document) {
        String safe = document.getName() == null ? "document" : document.getName().replaceAll("[^A-Za-z0-9._-]", "_");
        return safe + "." + fileExtension();
    }

    // Hook: default audit logging.
    protected void logExport(ExportRequest request, String filename) {
        log.info("Document exported: id={} format={} by={} filename={}",
                request.documentId(), request.format(), request.requestedBy(), filename);
    }
}
