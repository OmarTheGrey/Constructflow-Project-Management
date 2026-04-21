package com.constructflow.service;

import com.constructflow.dto.DailyLogRequestDTO;
import com.constructflow.dto.DailyLogResponseDTO;
import com.constructflow.model.DailyLog;
import com.constructflow.repository.DailyLogRepository;
import com.constructflow.service.mapping.DailyLogMapper;
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
    private final DailyLogMapper dailyLogMapper;

    public List<DailyLogResponseDTO> getLogsByTask(UUID taskId) {
        return dailyLogRepository.findByTaskId(taskId).stream()
                .map(dailyLogMapper::toResponse)
                .toList();
    }

    @Transactional
    public DailyLogResponseDTO createLog(DailyLogRequestDTO dto) {
        DailyLog log = new DailyLog();
        log.setTaskId(dto.getTaskId());
        log.setLogEntry(dto.getLogEntry());
        log.setDateCreated(LocalDateTime.now());
        return dailyLogMapper.toResponse(dailyLogRepository.save(log));
    }

    @Transactional
    public void deleteLog(UUID id) {
        dailyLogRepository.deleteById(id);
    }
}
