package com.constructflow.repository;

import com.constructflow.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByNameContainingIgnoreCase(String name);

    List<Project> findByStatus(String status);

    // 1. Aggregate: Total Budget
    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.budget) FROM Project p")
    Double getTotalBudget();

    // 2. Aggregate: Average Actual Cost
    @org.springframework.data.jpa.repository.Query("SELECT AVG(p.actualCost) FROM Project p")
    Double getAverageActualCost();

    // 3. Sub-query: Projects with budget higher than average
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Project p WHERE p.budget > (SELECT AVG(p2.budget) FROM Project p2)")
    List<Project> findHighValueProjects();
}
