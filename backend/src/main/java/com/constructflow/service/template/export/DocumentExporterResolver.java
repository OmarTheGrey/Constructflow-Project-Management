package com.constructflow.service.template.export;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class DocumentExporterResolver {

    private final Map<ExportFormat, AbstractDocumentExporter> exporters = new EnumMap<>(ExportFormat.class);

    public DocumentExporterResolver(PdfDocumentExporter pdf, CsvDocumentExporter csv, ZipArchiveExporter zip) {
        exporters.put(ExportFormat.PDF, pdf);
        exporters.put(ExportFormat.CSV, csv);
        exporters.put(ExportFormat.ZIP, zip);
    }

    public AbstractDocumentExporter forFormat(ExportFormat format) {
        AbstractDocumentExporter exporter = exporters.get(format);
        if (exporter == null) {
            throw new IllegalArgumentException("No exporter registered for format: " + format);
        }
        return exporter;
    }
}
