package com.constructflow.service.mapping;

import com.constructflow.dto.ProjectResponseDTO;
import com.constructflow.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    public ProjectResponseDTO toResponse(Project p) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setClient(p.getClient());
        dto.setLocation(p.getLocation());
        dto.setBudget(p.getBudget());
        dto.setActualCost(p.getActualCost());
        dto.setStartDate(p.getStartDate());
        dto.setEndDate(p.getEndDate());
        dto.setProgress(p.getProgress());
        dto.setStatus(p.getStatus());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setLastModifiedAt(p.getLastModifiedAt());
        dto.setObjectives(p.getObjectives());
        dto.setMilestones(p.getMilestones());
        return dto;
    }
}
