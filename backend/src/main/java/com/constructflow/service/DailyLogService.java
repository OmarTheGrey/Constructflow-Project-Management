package com.constructflow.service;

import com.constructflow.dto.DailyLogRequestDTO;
import com.constructflow.dto.DailyLogResponseDTO;
import com.constructflow.model.DailyLog;
import com.constructflow.repository.DailyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DailyLogService {
    private final DailyLogRepository dailyLogRepository;

    public List<DailyLogResponseDTO> getLogsByTask(UUID taskId) {
        return dailyLogRepository.findByTaskId(taskId).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional
    public DailyLogResponseDTO createLog(DailyLogRequestDTO dto) {
        DailyLog log = new DailyLog();
        log.setTaskId(dto.getTaskId());
        log.setLogEntry(dto.getLogEntry());
        log.setDateCreated(LocalDateTime.now());

        DailyLog saved = dailyLogRepository.save(log);
        return convertToResponseDTO(saved);
    }

    private DailyLogResponseDTO convertToResponseDTO(DailyLog entity) {
        DailyLogResponseDTO dto = new DailyLogResponseDTO();
        dto.setId(entity.getId());
        dto.setTaskId(entity.getTaskId());
        dto.setLogEntry(entity.getLogEntry());
        dto.setDateCreated(entity.getDateCreated());
        return dto;
    }

    @Transactional
    public void deleteLog(UUID id) {
        dailyLogRepository.deleteById(id);
    }
}
