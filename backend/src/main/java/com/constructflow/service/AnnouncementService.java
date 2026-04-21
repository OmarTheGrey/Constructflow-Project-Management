package com.constructflow.service;

import com.constructflow.dto.AnnouncementRequestDTO;
import com.constructflow.dto.AnnouncementResponseDTO;
import com.constructflow.model.Announcement;
import com.constructflow.repository.AnnouncementRepository;
import com.constructflow.service.mapping.AnnouncementMapper;
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
    private final AnnouncementMapper announcementMapper;

    public List<AnnouncementResponseDTO> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(announcementMapper::toResponse)
                .toList();
    }

    @Transactional
    public AnnouncementResponseDTO createAnnouncement(AnnouncementRequestDTO dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setPriority(dto.getPriority());
        announcement.setDatePosted(LocalDateTime.now());
        return announcementMapper.toResponse(announcementRepository.save(announcement));
    }

    @Transactional
    public void deleteAnnouncement(UUID id) {
        announcementRepository.deleteById(id);
    }
}
