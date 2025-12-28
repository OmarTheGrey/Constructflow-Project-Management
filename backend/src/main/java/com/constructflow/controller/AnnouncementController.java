package com.constructflow.controller;

import com.constructflow.dto.AnnouncementRequestDTO;
import com.constructflow.dto.AnnouncementResponseDTO;
import com.constructflow.dto.CommentRequestDTO;
import com.constructflow.dto.CommentResponseDTO;
import com.constructflow.service.AnnouncementService;
import com.constructflow.service.AnnouncementCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnnouncementController {
    private final AnnouncementService announcementService;
    private final AnnouncementCommentService commentService;

    @GetMapping
    public ResponseEntity<List<AnnouncementResponseDTO>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @PostMapping
    public ResponseEntity<AnnouncementResponseDTO> createAnnouncement(@Valid @RequestBody AnnouncementRequestDTO dto) {
        return new ResponseEntity<>(announcementService.createAnnouncement(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable UUID id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }

    // Comment endpoints
    @GetMapping("/{announcementId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable UUID announcementId) {
        return ResponseEntity.ok(commentService.getCommentsByAnnouncement(announcementId));
    }

    @PostMapping("/{announcementId}/comments")
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable UUID announcementId,
            @Valid @RequestBody CommentRequestDTO dto) {
        return new ResponseEntity<>(commentService.addComment(announcementId, dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
