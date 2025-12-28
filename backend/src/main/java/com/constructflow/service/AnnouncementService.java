package com.constructflow.service;

import com.constructflow.dto.AnnouncementRequestDTO;
import com.constructflow.dto.AnnouncementResponseDTO;
import com.constructflow.model.Announcement;
import com.constructflow.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public List<AnnouncementResponseDTO> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional
    public AnnouncementResponseDTO createAnnouncement(AnnouncementRequestDTO dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setPriority(dto.getPriority());
        announcement.setDatePosted(LocalDateTime.now());

        Announcement saved = announcementRepository.save(announcement);
        return convertToResponseDTO(saved);
    }

    private AnnouncementResponseDTO convertToResponseDTO(Announcement entity) {
        AnnouncementResponseDTO dto = new AnnouncementResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setPriority(entity.getPriority());
        dto.setDatePosted(entity.getDatePosted());
        return dto;
    }

    @Transactional
    public void deleteAnnouncement(UUID id) {
        announcementRepository.deleteById(id);
    }
}
