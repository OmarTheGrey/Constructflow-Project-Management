package com.constructflow.service.mapping;

import com.constructflow.dto.WorkLogResponseDTO;
import com.constructflow.model.WorkLog;
import org.springframework.stereotype.Component;

@Component
public class WorkLogMapper {
    public WorkLogResponseDTO toResponse(WorkLog log) {
        WorkLogResponseDTO dto = new WorkLogResponseDTO();
        dto.setId(log.getId());
        dto.setTaskId(log.getTaskId());
        dto.setHours(log.getHours());
        dto.setNotes(log.getNotes());
        dto.setDate(log.getDate());
        dto.setSubmittedBy(log.getSubmittedBy());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }
}
