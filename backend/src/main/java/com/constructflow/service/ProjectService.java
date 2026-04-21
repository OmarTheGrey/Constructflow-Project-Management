package com.constructflow.service;

import com.constructflow.dto.ProjectRequestDTO;
import com.constructflow.dto.ProjectResponseDTO;
import com.constructflow.exception.ResourceNotFoundException;
import com.constructflow.model.Project;
import com.constructflow.repository.ProjectRepository;
import com.constructflow.repository.TaskRepository;
import com.constructflow.service.factory.ProjectFactory;
import com.constructflow.service.mapping.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectMapper projectMapper;
    private final ProjectFactory projectFactory;

    public Page<ProjectResponseDTO> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable).map(projectMapper::toResponse);
    }

    public ProjectResponseDTO getProjectById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        return projectMapper.toResponse(project);
    }

    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        return projectMapper.toResponse(projectRepository.save(projectFactory.create(dto)));
    }

    @Transactional
    public ProjectResponseDTO updateProject(UUID id, ProjectRequestDTO dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        projectFactory.apply(project, dto);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(UUID id) {
        projectRepository.deleteById(id);
    }

    @Transactional
    public void updateProjectProgress(UUID projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) return;

        List<com.constructflow.model.Task> tasks = taskRepository.findByProjectId(projectId);
        if (tasks.isEmpty()) {
            project.setProgress(0.0);
        } else {
            long completed = tasks.stream()
                    .filter(t -> "Completed".equalsIgnoreCase(t.getStatus()))
                    .count();
            project.setProgress((double) completed / tasks.size() * 100);
        }

        BigDecimal totalActual = tasks.stream()
                .map(t -> t.getActualCost() != null ? t.getActualCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        project.setActualCost(totalActual);

        projectRepository.save(project);
    }
}
