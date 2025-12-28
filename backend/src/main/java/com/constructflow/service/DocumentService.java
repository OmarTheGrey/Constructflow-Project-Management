package com.constructflow.service;

import com.constructflow.dto.DocumentResponseDTO;
import com.constructflow.model.Document;
import com.constructflow.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final String UPLOAD_DIR = "uploads/";

    public List<DocumentResponseDTO> getDocumentsByProject(UUID projectId) {
        return documentRepository.findByProjectId(projectId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentResponseDTO uploadDocument(MultipartFile file, UUID projectId, String folder, String type)
            throws IOException {
        // Create upload dir if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Save file
        Files.copy(file.getInputStream(), filePath);

        // Save metadata
        Document document = new Document();
        document.setName(originalFilename);
        document.setType(type);
        document.setFolder(folder != null ? folder : "General");
        document.setProjectId(projectId);
        document.setUploadDate(LocalDateTime.now());

        long sizeInKb = file.getSize() / 1024;
        document.setSize(sizeInKb + " KB");

        return mapToResponseDTO(documentRepository.save(document));
    }

    @Transactional
    public void deleteDocument(UUID id) {
        documentRepository.deleteById(id);
        // In a real app, delete the file from disk too
    }

    private DocumentResponseDTO mapToResponseDTO(Document d) {
        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setId(d.getId());
        dto.setName(d.getName());
        dto.setType(d.getType());
        dto.setFolder(d.getFolder());
        dto.setUploadDate(d.getUploadDate());
        dto.setSize(d.getSize());
        return dto;
    }
}
