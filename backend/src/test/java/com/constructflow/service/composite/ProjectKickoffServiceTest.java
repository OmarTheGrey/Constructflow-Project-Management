package com.constructflow.service.composite;

import com.constructflow.dto.ProjectRequestDTO;
import com.constructflow.dto.ProjectResponseDTO;
import com.constructflow.dto.TaskRequestDTO;
import com.constructflow.dto.TaskResponseDTO;
import com.constructflow.dto.composite.ProjectKickoffRequestDTO;
import com.constructflow.dto.composite.ProjectKickoffRequestDTO.KickoffAllocation;
import com.constructflow.dto.composite.ProjectKickoffRequestDTO.KickoffTask;
import com.constructflow.dto.composite.ProjectKickoffResponseDTO;
import com.constructflow.service.ProjectService;
import com.constructflow.service.ResourceService;
import com.constructflow.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectKickoffServiceTest {

    @Mock private ProjectService projectService;
    @Mock private TaskService taskService;
    @Mock private ResourceService resourceService;

    @InjectMocks private ProjectKickoffService service;

    @Test
    void kickoff_createsProjectThenTasksThenAllocations() {
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();

        ProjectResponseDTO projectResp = new ProjectResponseDTO();
        projectResp.setId(projectId);
        projectResp.setName("Kickoff Project");
        when(projectService.createProject(any(ProjectRequestDTO.class))).thenReturn(projectResp);

        TaskResponseDTO taskResp = new TaskResponseDTO();
        taskResp.setId(taskId);
        when(taskService.createTask(any(TaskRequestDTO.class))).thenReturn(taskResp);

        ProjectKickoffRequestDTO req = new ProjectKickoffRequestDTO();
        ProjectRequestDTO p = new ProjectRequestDTO();
        p.setName("Kickoff Project");
        req.setProject(p);

        KickoffTask kt = new KickoffTask();
        kt.setName("Site survey");
        req.setInitialTasks(List.of(kt));

        KickoffAllocation ka = new KickoffAllocation();
        ka.setTaskIndex(0);
        ka.setResourceId(resourceId);
        ka.setQuantity(5.0);
        req.setInitialAllocations(List.of(ka));

        ProjectKickoffResponseDTO out = service.kickoff(req);

        verify(projectService, times(1)).createProject(any());
        verify(taskService, times(1)).createTask(any());
        verify(resourceService, times(1)).allocateResource(eq(taskId), eq(resourceId), eq(5.0));

        assertThat(out.getProject().getId()).isEqualTo(projectId);
        assertThat(out.getTasks()).hasSize(1);
        assertThat(out.getAllocations()).hasSize(1);
        assertThat(out.getAllocations().get(0).getStatus()).isEqualTo("ALLOCATED");
    }

    @Test
    void kickoff_invalidTaskIndex_recordsFailureWithoutCallingAllocator() {
        ProjectResponseDTO projectResp = new ProjectResponseDTO();
        projectResp.setId(UUID.randomUUID());
        when(projectService.createProject(any())).thenReturn(projectResp);

        ProjectKickoffRequestDTO req = new ProjectKickoffRequestDTO();
        ProjectRequestDTO p = new ProjectRequestDTO();
        p.setName("P");
        req.setProject(p);
        req.setInitialTasks(List.of());

        KickoffAllocation ka = new KickoffAllocation();
        ka.setTaskIndex(0);
        ka.setResourceId(UUID.randomUUID());
        ka.setQuantity(1.0);
        req.setInitialAllocations(List.of(ka));

        ProjectKickoffResponseDTO out = service.kickoff(req);

        assertThat(out.getAllocations()).hasSize(1);
        assertThat(out.getAllocations().get(0).getStatus()).startsWith("FAILED:");
        verify(resourceService, times(0)).allocateResource(any(), any(), any());
    }
}
