package com.constructflow.dto.composite;

import com.constructflow.dto.ProjectResponseDTO;
import com.constructflow.dto.TaskResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectKickoffResponseDTO {
    private ProjectResponseDTO project;
    private List<TaskResponseDTO> tasks;
    private List<AllocationResult> allocations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AllocationResult {
        private UUID taskId;
        private UUID resourceId;
        private Double quantity;
        private String status; // "ALLOCATED" or "FAILED:<reason>"
    }
}
