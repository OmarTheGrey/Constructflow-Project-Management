package com.constructflow.service;

import com.constructflow.dto.DailyReportRequestDTO;
import com.constructflow.dto.DailyReportResponseDTO;
import com.constructflow.model.DailyReport;
import com.constructflow.repository.DailyReportRepository;
import com.constructflow.service.mapping.DailyReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyReportService {
    private final DailyReportRepository dailyReportRepository;
    private final DailyReportMapper dailyReportMapper;

    public List<DailyReportResponseDTO> getReportsByProject(UUID projectId) {
        return dailyReportRepository.findByProjectId(projectId).stream()
                .map(dailyReportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DailyReportResponseDTO createReport(DailyReportRequestDTO dto) {
        DailyReport report = new DailyReport();
        report.setProjectId(dto.getProjectId());
        report.setActivities(dto.getActivities());
        report.setIssues(dto.getIssues());
        report.setPhotos(dto.getPhotos());
        report.setCompletionPercentage(dto.getCompletionPercentage());
        report.setSubmittedBy(dto.getSubmittedBy());
        return dailyReportMapper.toResponse(dailyReportRepository.save(report));
    }
}
