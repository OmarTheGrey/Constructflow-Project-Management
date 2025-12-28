package com.constructflow.repository;

import com.constructflow.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByProjectId(UUID projectId);
    List<Document> findByNameContainingIgnoreCase(String name);
}
