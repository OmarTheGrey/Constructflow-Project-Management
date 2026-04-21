package com.constructflow.service.template.export;

import com.constructflow.model.Document;
import com.constructflow.repository.DocumentRepository;
import com.constructflow.service.storage.DocumentStorage;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PdfDocumentExporter extends AbstractDocumentExporter {

    public PdfDocumentExporter(DocumentRepository documentRepository, DocumentStorage documentStorage) {
        super(documentRepository, documentStorage);
    }

    @Override
    protected byte[] transform(Document document, byte[] rawContent) {
        // Minimal PDF header — real implementations would use iText / PDFBox.
        // The point of the template method is structure; the transform step is pluggable.
        String header = "%PDF-1.4\n% ConstructFlow export — " + document.getName() + "\n";
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[headerBytes.length + rawContent.length];
        System.arraycopy(headerBytes, 0, out, 0, headerBytes.length);
        System.arraycopy(rawContent, 0, out, headerBytes.length, rawContent.length);
        return out;
    }

    @Override protected String contentType()   { return "application/pdf"; }
    @Override protected String fileExtension() { return "pdf"; }
}
