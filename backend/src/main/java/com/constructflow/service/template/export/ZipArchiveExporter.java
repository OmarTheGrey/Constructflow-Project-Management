package com.constructflow.service.template.export;

import com.constructflow.model.Document;
import com.constructflow.repository.DocumentRepository;
import com.constructflow.service.storage.DocumentStorage;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipArchiveExporter extends AbstractDocumentExporter {

    public ZipArchiveExporter(DocumentRepository documentRepository, DocumentStorage documentStorage) {
        super(documentRepository, documentStorage);
    }

    @Override
    protected byte[] transform(Document document, byte[] rawContent) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(buffer)) {
            ZipEntry entry = new ZipEntry(document.getName() == null ? "document.bin" : document.getName());
            zip.putNextEntry(entry);
            zip.write(rawContent);
            zip.closeEntry();
        }
        return buffer.toByteArray();
    }

    @Override protected String contentType()   { return "application/zip"; }
    @Override protected String fileExtension() { return "zip"; }
}
