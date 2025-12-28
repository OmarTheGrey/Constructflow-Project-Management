package com.constructflow.repository;

import com.constructflow.model.DailyReport;
import com.constructflow.model.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, UUID> {
    List<DailyReport> findByProjectId(UUID projectId);
}
