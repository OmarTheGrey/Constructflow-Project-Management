package com.constructflow.service.mapping;

import com.constructflow.dto.TaskResponseDTO;
import com.constructflow.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskResponseDTO toResponse(Task t) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setProjectId(t.getProjectId());
        dto.setAssignee(t.getAssignee());
        dto.setDueDate(t.getDueDate());
        dto.setStatus(t.getStatus());
        dto.setPriority(t.getPriority());
        dto.setDescription(t.getDescription());
        dto.setActualCost(t.getActualCost());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setDependencies(t.getDependencies());
        return dto;
    }
}
