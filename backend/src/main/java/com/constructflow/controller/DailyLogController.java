package com.constructflow.controller;

import com.constructflow.dto.DailyLogRequestDTO;
import com.constructflow.dto.DailyLogResponseDTO;
import com.constructflow.service.DailyLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DailyLogController {
    private final DailyLogService dailyLogService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<DailyLogResponseDTO>> getLogsByTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(dailyLogService.getLogsByTask(taskId));
    }

    @PostMapping
    public ResponseEntity<DailyLogResponseDTO> createLog(@Valid @RequestBody DailyLogRequestDTO dto) {
        return new ResponseEntity<>(dailyLogService.createLog(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable UUID id) {
        dailyLogService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}
