package com.constructflow.service.mapping;

import com.constructflow.dto.DocumentResponseDTO;
import com.constructflow.model.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {
    public DocumentResponseDTO toResponse(Document d) {
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
