package com.constructflow.controller;

import com.constructflow.dto.ExecutiveSummaryDTO;
import com.constructflow.service.ReportService;
import com.constructflow.service.factory.report.ReportKind;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<ExecutiveSummaryDTO> getExecutiveSummary() {
        return ResponseEntity.ok(reportService.executiveSummary());
    }

    @GetMapping("/{kind}")
    public ResponseEntity<Map<String, Object>> getReport(@PathVariable ReportKind kind) {
        return ResponseEntity.ok(reportService.build(kind));
    }
}
