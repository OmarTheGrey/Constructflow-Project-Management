package com.constructflow.controller;

import com.constructflow.dto.DailyReportRequestDTO;
import com.constructflow.dto.DailyReportResponseDTO;
import com.constructflow.service.DailyReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/daily-reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DailyReportController {
    private final DailyReportService dailyReportService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<DailyReportResponseDTO>> getReportsByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(dailyReportService.getReportsByProject(projectId));
    }

    @PostMapping
    public ResponseEntity<DailyReportResponseDTO> createReport(@Valid @RequestBody DailyReportRequestDTO dto) {
        return new ResponseEntity<>(dailyReportService.createReport(dto), HttpStatus.CREATED);
    }
}
