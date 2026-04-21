package com.constructflow.service;

import com.constructflow.dto.WorkLogRequestDTO;
import com.constructflow.dto.WorkLogResponseDTO;
import com.constructflow.model.WorkLog;
import com.constructflow.repository.WorkLogRepository;
import com.constructflow.service.mapping.WorkLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkLogService {
    private final WorkLogRepository workLogRepository;
    private final WorkLogMapper workLogMapper;

    public List<WorkLogResponseDTO> getLogsByTask(UUID taskId) {
        return workLogRepository.findByTaskId(taskId).stream()
                .map(workLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkLogResponseDTO createLog(WorkLogRequestDTO dto) {
        WorkLog log = new WorkLog();
        log.setTaskId(dto.getTaskId());
        log.setHours(dto.getHours());
        log.setNotes(dto.getNotes());
        log.setDate(dto.getDate());
        log.setSubmittedBy(dto.getSubmittedBy());
        return workLogMapper.toResponse(workLogRepository.save(log));
    }
}
