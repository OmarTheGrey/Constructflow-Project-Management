package com.constructflow.service.factory;

import com.constructflow.dto.TaskRequestDTO;
import com.constructflow.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskFactory implements EntityFactory<Task, TaskRequestDTO> {

    @Override
    public Task create(TaskRequestDTO dto) {
        Task task = new Task();
        apply(task, dto);
        if (task.getStatus() == null)   task.setStatus("Pending");
        if (task.getPriority() == null) task.setPriority("Normal");
        return task;
    }

    @Override
    public void apply(Task task, TaskRequestDTO dto) {
        if (dto.getName() != null)         task.setName(dto.getName());
        if (dto.getProjectId() != null)    task.setProjectId(dto.getProjectId());
        if (dto.getAssignee() != null)     task.setAssignee(dto.getAssignee());
        if (dto.getDueDate() != null)      task.setDueDate(dto.getDueDate());
        if (dto.getStatus() != null)       task.setStatus(dto.getStatus());
        if (dto.getPriority() != null)     task.setPriority(dto.getPriority());
        if (dto.getDescription() != null)  task.setDescription(dto.getDescription());
        if (dto.getActualCost() != null)   task.setActualCost(dto.getActualCost());
        if (dto.getDependencies() != null) task.setDependencies(dto.getDependencies());
    }
}
