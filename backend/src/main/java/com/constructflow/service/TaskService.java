package com.constructflow.service;

import com.constructflow.dto.TaskRequestDTO;
import com.constructflow.dto.TaskResponseDTO;
import com.constructflow.exception.ResourceNotFoundException;
import com.constructflow.model.Task;
import com.constructflow.repository.TaskRepository;
import com.constructflow.service.events.TaskMutatedEvent;
import com.constructflow.service.factory.TaskFactory;
import com.constructflow.service.mapping.TaskMapper;
import com.constructflow.service.strategy.prioritisation.PrioritisationKey;
import com.constructflow.service.strategy.prioritisation.PrioritisationStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final TaskMapper taskMapper;
    private final TaskFactory taskFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final PrioritisationStrategyResolver prioritisationResolver;

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
        Task saved = taskRepository.save(taskFactory.create(dto));
        eventPublisher.publishEvent(new TaskMutatedEvent(saved.getProjectId()));
        return taskMapper.toResponse(saved);
    }

    @Transactional
    public TaskResponseDTO updateTask(UUID id, TaskRequestDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskFactory.apply(task, dto);
        Task updated = taskRepository.save(task);
        eventPublisher.publishEvent(new TaskMutatedEvent(updated.getProjectId()));
        return taskMapper.toResponse(updated);
    }

    @Transactional
    public void deleteTask(UUID id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            UUID projectId = task.getProjectId();
            taskRepository.deleteById(id);
            eventPublisher.publishEvent(new TaskMutatedEvent(projectId));
        }
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> searchTasks(String query) {
        return taskRepository.findByNameContainingIgnoreCase(query).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getCriticalTasks(PrioritisationKey sortKey) {
        List<com.constructflow.model.Task> raw = taskRepository.findCriticalTasks();
        return prioritisationResolver.resolve(sortKey).prioritise(raw).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }
}
