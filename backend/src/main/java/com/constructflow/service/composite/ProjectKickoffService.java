package com.constructflow.service.composite;

import com.constructflow.dto.ProjectResponseDTO;
import com.constructflow.dto.TaskRequestDTO;
import com.constructflow.dto.TaskResponseDTO;
import com.constructflow.dto.composite.ProjectKickoffRequestDTO;
import com.constructflow.dto.composite.ProjectKickoffResponseDTO;
import com.constructflow.dto.composite.ProjectKickoffResponseDTO.AllocationResult;
import com.constructflow.service.ProjectService;
import com.constructflow.service.ResourceService;
import com.constructflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectKickoffService {
    private static final Logger log = LoggerFactory.getLogger(ProjectKickoffService.class);

    private final ProjectService projectService;
    private final TaskService taskService;
    private final ResourceService resourceService;

    @Transactional
    public ProjectKickoffResponseDTO kickoff(ProjectKickoffRequestDTO req) {
        // 1) Create project
        ProjectResponseDTO project = projectService.createProject(req.getProject());
        log.info("Kickoff: created project id={} name='{}'", project.getId(), project.getName());

        // 2) Create initial tasks (if any)
        List<TaskResponseDTO> tasks = new ArrayList<>();
        List<ProjectKickoffRequestDTO.KickoffTask> taskSpecs =
                req.getInitialTasks() == null ? Collections.emptyList() : req.getInitialTasks();
        for (ProjectKickoffRequestDTO.KickoffTask spec : taskSpecs) {
            TaskRequestDTO t = new TaskRequestDTO();
            t.setName(spec.getName());
            t.setProjectId(project.getId());
            t.setAssignee(spec.getAssignee());
            t.setDueDate(spec.getDueDate());
            t.setStatus(spec.getStatus());
            t.setPriority(spec.getPriority());
            t.setDescription(spec.getDescription());
            tasks.add(taskService.createTask(t));
        }
        log.info("Kickoff: created {} task(s)", tasks.size());

        // 3) Allocate resources (best-effort: failures are recorded, not rolled back individually —
        //    the whole composite still rolls back if any allocation throws an unchecked exception)
        List<AllocationResult> results = new ArrayList<>();
        List<ProjectKickoffRequestDTO.KickoffAllocation> allocs =
                req.getInitialAllocations() == null ? Collections.emptyList() : req.getInitialAllocations();
        for (ProjectKickoffRequestDTO.KickoffAllocation a : allocs) {
            if (a.getTaskIndex() == null || a.getTaskIndex() < 0 || a.getTaskIndex() >= tasks.size()) {
                results.add(new AllocationResult(null, a.getResourceId(), a.getQuantity(),
                        "FAILED:taskIndex out of range"));
                continue;
            }
            var taskId = tasks.get(a.getTaskIndex()).getId();
            resourceService.allocateResource(taskId, a.getResourceId(), a.getQuantity());
            results.add(new AllocationResult(taskId, a.getResourceId(), a.getQuantity(), "ALLOCATED"));
        }
        log.info("Kickoff: processed {} allocation(s)", results.size());

        return new ProjectKickoffResponseDTO(project, tasks, results);
    }
}
