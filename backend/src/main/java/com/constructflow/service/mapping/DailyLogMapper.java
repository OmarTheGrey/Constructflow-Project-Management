package com.constructflow.service.mapping;

import com.constructflow.dto.DailyLogResponseDTO;
import com.constructflow.model.DailyLog;
import org.springframework.stereotype.Component;

@Component
public class DailyLogMapper {
    public DailyLogResponseDTO toResponse(DailyLog entity) {
        DailyLogResponseDTO dto = new DailyLogResponseDTO();
        dto.setId(entity.getId());
        dto.setTaskId(entity.getTaskId());
        dto.setLogEntry(entity.getLogEntry());
        dto.setDateCreated(entity.getDateCreated());
        return dto;
    }
}
