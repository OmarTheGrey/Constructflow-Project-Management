package com.constructflow.repository;

import com.constructflow.model.TaskAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskAllocationRepository extends JpaRepository<TaskAllocation, UUID> {
    List<TaskAllocation> findByTaskId(UUID taskId);

    List<TaskAllocation> findByResourceId(UUID resourceId);
}
