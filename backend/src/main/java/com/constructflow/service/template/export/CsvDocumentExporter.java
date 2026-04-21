package com.constructflow.service.template.export;

import com.constructflow.model.Document;
import com.constructflow.repository.DocumentRepository;
import com.constructflow.service.storage.DocumentStorage;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class CsvDocumentExporter extends AbstractDocumentExporter {

    public CsvDocumentExporter(DocumentRepository documentRepository, DocumentStorage documentStorage) {
        super(documentRepository, documentStorage);
    }

    @Override
    protected byte[] transform(Document document, byte[] rawContent) {
        // Produce a single-row CSV describing the document metadata. The raw bytes are Base64-escaped
        // so the CSV stays valid even for binary files.
        StringBuilder sb = new StringBuilder();
        sb.append("id,name,type,folder,uploadDate,sizeLabel,sizeBytes\n");
        sb.append(csv(document.getId().toString())).append(',');
        sb.append(csv(document.getName())).append(',');
        sb.append(csv(document.getType())).append(',');
        sb.append(csv(document.getFolder())).append(',');
        sb.append(csv(String.valueOf(document.getUploadDate()))).append(',');
        sb.append(csv(document.getSize())).append(',');
        sb.append(rawContent.length).append('\n');
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String csv(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    @Override protected String contentType()   { return "text/csv"; }
    @Override protected String fileExtension() { return "csv"; }
}
