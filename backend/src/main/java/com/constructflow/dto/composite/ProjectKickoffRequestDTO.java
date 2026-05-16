package com.constructflow.dto.composite;

import com.constructflow.dto.ProjectRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProjectKickoffRequestDTO {

    @Valid
    @NotNull(message = "project payload is required")
    private ProjectRequestDTO project;

    @Valid
    private List<KickoffTask> initialTasks;

    @Valid
    private List<KickoffAllocation> initialAllocations;

    @Data
    public static class KickoffTask {
        private String name;
        private String assignee;
        private java.time.LocalDate dueDate;
        private String status;
        private String priority;
        private String description;
    }

    @Data
    public static class KickoffAllocation {
        // Index into initialTasks (0-based). The composite resolves it to the task UUID after task creation.
        @NotNull private Integer taskIndex;
        @NotNull private UUID resourceId;
        @NotNull private Double quantity;
    }
}
