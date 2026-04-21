package com.constructflow.service;

import com.constructflow.dto.DocumentResponseDTO;
import com.constructflow.model.Document;
import com.constructflow.repository.DocumentRepository;
import com.constructflow.service.mapping.DocumentMapper;
import com.constructflow.service.storage.DocumentStorage;
import com.constructflow.service.storage.StoredFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final DocumentStorage documentStorage;

    public List<DocumentResponseDTO> getDocumentsByProject(UUID projectId) {
        return documentRepository.findByProjectId(projectId).stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentResponseDTO uploadDocument(MultipartFile file, UUID projectId, String folder, String type)
            throws IOException {
        StoredFile stored = documentStorage.store(file);

        Document document = new Document();
        document.setName(file.getOriginalFilename());
        document.setType(type);
        document.setFolder(folder != null ? folder : "General");
        document.setProjectId(projectId);
        document.setUploadDate(LocalDateTime.now());
        document.setSize(stored.sizeBytes() / 1024 + " KB");
        document.setStorageKey(stored.storageKey());
        return documentMapper.toResponse(documentRepository.save(document));
    }

    @Transactional
    public void deleteDocument(UUID id) {
        documentRepository.findById(id).ifPresent(doc -> {
            try {
                if (doc.getStorageKey() != null) {
                    documentStorage.delete(doc.getStorageKey());
                }
            } catch (IOException ignored) {}
            documentRepository.delete(doc);
        });
    }
}
