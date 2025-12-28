package com.constructflow.repository;

import com.constructflow.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectId(UUID projectId);

    List<Task> findByNameContainingIgnoreCase(String name);

    List<Task> findByPriority(String priority);

    // 4. Sub-query: Tasks with actual cost higher than average task cost
    @org.springframework.data.jpa.repository.Query("SELECT t FROM Task t WHERE t.actualCost > (SELECT AVG(t2.actualCost) FROM Task t2)")
    List<Task> findExpensiveTasks();

    // Find tasks by Critical priority
    @org.springframework.data.jpa.repository.Query("SELECT t FROM Task t WHERE LOWER(t.priority) = 'critical' ORDER BY t.dueDate")
    List<Task> findCriticalTasks();
}
