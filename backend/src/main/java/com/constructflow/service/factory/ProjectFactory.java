package com.constructflow.service.factory;

import com.constructflow.config.AppProperties;
import com.constructflow.dto.ProjectRequestDTO;
import com.constructflow.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ProjectFactory implements EntityFactory<Project, ProjectRequestDTO> {

    private final AppProperties appProperties;

    @Override
    public Project create(ProjectRequestDTO dto) {
        Project project = new Project();
        apply(project, dto);
        if (project.getStatus() == null)     project.setStatus(appProperties.getStatus().getActive());
        if (project.getActualCost() == null) project.setActualCost(BigDecimal.ZERO);
        if (project.getProgress() == null)   project.setProgress(0.0);
        return project;
    }

    @Override
    public void apply(Project project, ProjectRequestDTO dto) {
        if (dto.getName() != null)       project.setName(dto.getName());
        if (dto.getClient() != null)     project.setClient(dto.getClient());
        if (dto.getLocation() != null)   project.setLocation(dto.getLocation());
        if (dto.getBudget() != null)     project.setBudget(dto.getBudget());
        if (dto.getStartDate() != null)  project.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null)    project.setEndDate(dto.getEndDate());
        if (dto.getStatus() != null)     project.setStatus(dto.getStatus());
        if (dto.getObjectives() != null) project.setObjectives(dto.getObjectives());
        if (dto.getMilestones() != null) project.setMilestones(dto.getMilestones());
    }
}
