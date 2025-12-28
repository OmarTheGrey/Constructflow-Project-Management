package com.constructflow.controller;

import com.constructflow.dto.WorkLogRequestDTO;
import com.constructflow.dto.WorkLogResponseDTO;
import com.constructflow.service.WorkLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/work-logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkLogController {
    private final WorkLogService workLogService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<WorkLogResponseDTO>> getLogsByTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(workLogService.getLogsByTask(taskId));
    }

    @PostMapping
    public ResponseEntity<WorkLogResponseDTO> createLog(@Valid @RequestBody WorkLogRequestDTO dto) {
        return new ResponseEntity<>(workLogService.createLog(dto), HttpStatus.CREATED);
    }
}
