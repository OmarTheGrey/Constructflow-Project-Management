package com.constructflow.controller;

import com.constructflow.dto.StakeholderRequestDTO;
import com.constructflow.dto.StakeholderResponseDTO;
import com.constructflow.service.StakeholderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stakeholders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StakeholderController {
    private final StakeholderService stakeholderService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<StakeholderResponseDTO>> getStakeholdersByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(stakeholderService.getStakeholdersByProject(projectId));
    }

    @PostMapping
    public ResponseEntity<StakeholderResponseDTO> createStakeholder(@Valid @RequestBody StakeholderRequestDTO dto) {
        return new ResponseEntity<>(stakeholderService.createStakeholder(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStakeholder(@PathVariable UUID id) {
        stakeholderService.deleteStakeholder(id);
        return ResponseEntity.noContent().build();
    }
}
