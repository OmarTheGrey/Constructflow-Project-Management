package com.constructflow.controller;

import com.constructflow.dto.ResourceRequestDTO;
import com.constructflow.dto.ResourceResponseDTO;
import com.constructflow.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping
    public ResponseEntity<Page<ResourceResponseDTO>> getAllResources(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(resourceService.getAllResources(pageable));
    }

    @PostMapping
    public ResponseEntity<ResourceResponseDTO> createResource(@Valid @RequestBody ResourceRequestDTO dto) {
        return new ResponseEntity<>(resourceService.createResource(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponseDTO> updateResource(@PathVariable UUID id,
            @Valid @RequestBody ResourceRequestDTO dto) {
        return ResponseEntity.ok(resourceService.updateResource(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable UUID id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/allocate")
    public ResponseEntity<Void> allocateResource(
            @RequestParam UUID taskId,
            @RequestParam UUID resourceId,
            @RequestParam Double quantity) {
        resourceService.allocateResource(taskId, resourceId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/inventory")
    public ResponseEntity<Void> updateInventory(
            @PathVariable UUID id,
            @RequestParam Double quantityChange,
            @RequestParam String reason) {
        resourceService.updateInventory(id, quantityChange, reason);
        return ResponseEntity.ok().build();
    }
}
