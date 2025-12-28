package com.constructflow.repository;

import com.constructflow.model.DailyLog;
import com.constructflow.model.Task;
import com.constructflow.model.Project;
import com.constructflow.model.Resource;
import com.constructflow.model.TaskAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface DailyLogRepository extends JpaRepository<DailyLog, UUID> {
    List<DailyLog> findByTaskId(UUID taskId);

    // 6. Complex Join (>2 tables): Find daily logs for tasks in Projects located in
    // 'New York' (Example condition)
    // DailyLog -> Task -> Project
    @org.springframework.data.jpa.repository.Query("SELECT dl FROM DailyLog dl " +
            "JOIN Task t ON dl.taskId = t.id " +
            "JOIN Project p ON t.projectId = p.id " +
            "WHERE p.location = :location")
    List<DailyLog> findLogsByProjectLocation(
            @org.springframework.web.bind.annotation.RequestParam("location") String location);

    // 7. Join: Tasks with specific resource category allocated
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT t FROM Task t " +
            "JOIN TaskAllocation ta ON t.id = ta.taskId " +
            "JOIN Resource r ON ta.resourceId = r.id " +
            "WHERE r.category = :category")
    List<Task> findTasksUsingResourceCategory(
            @org.springframework.web.bind.annotation.RequestParam("category") String category);
}
