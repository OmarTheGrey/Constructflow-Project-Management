package com.constructflow.service.mapping;

import com.constructflow.dto.DailyReportResponseDTO;
import com.constructflow.model.DailyReport;
import org.springframework.stereotype.Component;

@Component
public class DailyReportMapper {
    public DailyReportResponseDTO toResponse(DailyReport r) {
        DailyReportResponseDTO dto = new DailyReportResponseDTO();
        dto.setId(r.getId());
        dto.setProjectId(r.getProjectId());
        dto.setActivities(r.getActivities());
        dto.setIssues(r.getIssues());
        dto.setPhotos(r.getPhotos());
        dto.setCompletionPercentage(r.getCompletionPercentage());
        dto.setSubmittedBy(r.getSubmittedBy());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
