package com.constructflow.service;

import com.constructflow.dto.TaskRequestDTO;
import com.constructflow.dto.TaskResponseDTO;
import com.constructflow.exception.ResourceNotFoundException;
import com.constructflow.model.Task;
import com.constructflow.repository.TaskRepository;
import com.constructflow.service.mapping.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final TaskMapper taskMapper;

    public Page<TaskResponseDTO> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(taskMapper::toResponse);
    }

    public List<TaskResponseDTO> getTasksByProject(UUID projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        Task task = new Task();
        task.setName(dto.getName());
        task.setProjectId(dto.getProjectId());
        task.setAssignee(dto.getAssignee());
        task.setDueDate(dto.getDueDate());
        task.setStatus(dto.getStatus() != null ? dto.getStatus() : "Pending");
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : "Normal");
        task.setDescription(dto.getDescription());
        task.setActualCost(dto.getActualCost());
        task.setDependencies(dto.getDependencies());

        Task saved = taskRepository.save(task);
        projectService.updateProjectProgress(dto.getProjectId());
        return taskMapper.toResponse(saved);
    }

    @Transactional
    public TaskResponseDTO updateTask(UUID id, TaskRequestDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (dto.getName() != null)         task.setName(dto.getName());
        if (dto.getAssignee() != null)     task.setAssignee(dto.getAssignee());
        if (dto.getDueDate() != null)      task.setDueDate(dto.getDueDate());
        if (dto.getStatus() != null)       task.setStatus(dto.getStatus());
        if (dto.getPriority() != null)     task.setPriority(dto.getPriority());
        if (dto.getDescription() != null)  task.setDescription(dto.getDescription());
        if (dto.getActualCost() != null)   task.setActualCost(dto.getActualCost());
        if (dto.getDependencies() != null) task.setDependencies(dto.getDependencies());

        Task updated = taskRepository.save(task);
        projectService.updateProjectProgress(task.getProjectId());
        return taskMapper.toResponse(updated);
    }

    @Transactional
    public void deleteTask(UUID id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            UUID projectId = task.getProjectId();
            taskRepository.deleteById(id);
            projectService.updateProjectProgress(projectId);
        }
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> searchTasks(String query) {
        return taskRepository.findByNameContainingIgnoreCase(query).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getCriticalTasks() {
        return taskRepository.findCriticalTasks().stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }
}
