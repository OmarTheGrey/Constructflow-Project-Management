package com.constructflow.repository;

import com.constructflow.model.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, UUID> {
    List<WorkLog> findByTaskId(UUID taskId);
}
