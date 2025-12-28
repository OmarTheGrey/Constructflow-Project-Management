package com.constructflow.repository;

import com.constructflow.model.Stakeholder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface StakeholderRepository extends JpaRepository<Stakeholder, UUID> {
    List<Stakeholder> findByProjectId(UUID projectId);
}
