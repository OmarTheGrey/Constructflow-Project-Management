package com.constructflow.controller;

import com.constructflow.dto.DocumentResponseDTO;
import com.constructflow.service.DocumentService;
import lombok.RequiredArgsConstructor;
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
@CrossOrigin(origins = "*")
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<DocumentResponseDTO>> getDocumentsByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(documentService.getDocumentsByProject(projectId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") UUID projectId,
            @RequestParam("folder") String folder,
            @RequestParam("type") String type) throws IOException { // Should handle exception properly
        return new ResponseEntity<>(documentService.uploadDocument(file, projectId, folder, type), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
