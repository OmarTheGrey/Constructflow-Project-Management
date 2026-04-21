package com.constructflow.controller;

import com.constructflow.dto.DocumentResponseDTO;
import com.constructflow.service.DocumentService;
import com.constructflow.service.template.export.DocumentExporterResolver;
import com.constructflow.service.template.export.ExportFormat;
import com.constructflow.service.template.export.ExportRequest;
import com.constructflow.service.template.export.ExportResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentExporterResolver exporterResolver;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<DocumentResponseDTO>> getDocumentsByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(documentService.getDocumentsByProject(projectId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") UUID projectId,
            @RequestParam("folder") String folder,
            @RequestParam("type") String type) throws IOException {
        return new ResponseEntity<>(documentService.uploadDocument(file, projectId, folder, type), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportDocument(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "PDF") ExportFormat format,
            @RequestParam(defaultValue = "system") String requestedBy) throws IOException {
        ExportResult result = exporterResolver.forFormat(format)
                .export(new ExportRequest(id, format, requestedBy));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.filename() + "\"")
                .contentType(MediaType.parseMediaType(result.contentType()))
                .body(result.bytes());
    }
}
