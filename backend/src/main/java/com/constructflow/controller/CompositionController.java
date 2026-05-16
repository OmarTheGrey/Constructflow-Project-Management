package com.constructflow.controller;

import com.constructflow.dto.composite.ProjectKickoffRequestDTO;
import com.constructflow.dto.composite.ProjectKickoffResponseDTO;
import com.constructflow.service.composite.ProjectKickoffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compositions")
@RequiredArgsConstructor
@Tag(name = "Composite Services", description = "Workflow endpoints that orchestrate multiple atomic services.")
public class CompositionController {

    private final ProjectKickoffService projectKickoffService;

    @PostMapping("/project-kickoff")
    @Operation(summary = "Kick off a new project: create project + initial tasks + initial allocations.",
            description = "Composite workflow that calls ProjectService, TaskService and ResourceService " +
                    "in a single transaction. If any underlying call fails, the whole workflow rolls back.")
    public ResponseEntity<ProjectKickoffResponseDTO> kickoffProject(
            @Valid @RequestBody ProjectKickoffRequestDTO request) {
        return new ResponseEntity<>(projectKickoffService.kickoff(request), HttpStatus.CREATED);
    }
}
