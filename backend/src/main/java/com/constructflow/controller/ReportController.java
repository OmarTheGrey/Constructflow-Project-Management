package com.constructflow.controller;

import com.constructflow.dto.ExecutiveSummaryDTO;
import com.constructflow.service.GlobalReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final GlobalReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<ExecutiveSummaryDTO> getExecutiveSummary() {
        return ResponseEntity.ok(reportService.generateExecutiveSummary());
    }
}
