package com.constructflow.repository;

import com.constructflow.model.DailyLog;
import com.constructflow.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DailyLogRepository extends JpaRepository<DailyLog, UUID> {
    List<DailyLog> findByTaskId(UUID taskId);

    // Complex join: DailyLog -> Task -> Project, filtered by project location.
    @Query("SELECT dl FROM DailyLog dl " +
            "JOIN Task t ON dl.taskId = t.id " +
            "JOIN Project p ON t.projectId = p.id " +
            "WHERE p.location = :location")
    List<DailyLog> findLogsByProjectLocation(@Param("location") String location);

    // Tasks with a specific resource category allocated.
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN TaskAllocation ta ON t.id = ta.taskId " +
            "JOIN Resource r ON ta.resourceId = r.id " +
            "WHERE r.category = :category")
    List<Task> findTasksUsingResourceCategory(@Param("category") String category);
}
