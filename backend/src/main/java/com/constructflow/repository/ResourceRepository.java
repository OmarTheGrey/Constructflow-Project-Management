package com.constructflow.repository;

import com.constructflow.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    List<Resource> findByNameContainingIgnoreCase(String name);

    List<Resource> findByCategory(String category);

    // 5. Complex Join (>2 tables): Find resources allocated to 'Active' projects
    // Resource -> TaskAllocation -> Task -> Project
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT r FROM Resource r " +
            "JOIN TaskAllocation ta ON r.id = ta.resourceId " +
            "JOIN Task t ON ta.taskId = t.id " +
            "JOIN Project p ON t.projectId = p.id " +
            "WHERE p.status = 'Active'")
    List<Resource> findResourcesInActiveProjects();
}
