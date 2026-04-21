package com.constructflow.service;

import com.constructflow.dto.DocumentResponseDTO;
import com.constructflow.model.Document;
import com.constructflow.repository.DocumentRepository;
import com.constructflow.service.mapping.DocumentMapper;
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
    private final DocumentMapper documentMapper;
    private final String UPLOAD_DIR = "uploads/";

    public List<DocumentResponseDTO> getDocumentsByProject(UUID projectId) {
        return documentRepository.findByProjectId(projectId).stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentResponseDTO uploadDocument(MultipartFile file, UUID projectId, String folder, String type)
            throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String uniqueFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), uploadPath.resolve(uniqueFilename));

        Document document = new Document();
        document.setName(file.getOriginalFilename());
        document.setType(type);
        document.setFolder(folder != null ? folder : "General");
        document.setProjectId(projectId);
        document.setUploadDate(LocalDateTime.now());
        document.setSize(file.getSize() / 1024 + " KB");
        return documentMapper.toResponse(documentRepository.save(document));
    }

    @Transactional
    public void deleteDocument(UUID id) {
        documentRepository.deleteById(id);
    }
}
