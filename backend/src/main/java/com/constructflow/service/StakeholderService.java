package com.constructflow.service;

import com.constructflow.dto.StakeholderRequestDTO;
import com.constructflow.dto.StakeholderResponseDTO;
import com.constructflow.model.Stakeholder;
import com.constructflow.repository.StakeholderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StakeholderService {
    private final StakeholderRepository stakeholderRepository;

    public List<StakeholderResponseDTO> getStakeholdersByProject(UUID projectId) {
        return stakeholderRepository.findByProjectId(projectId).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional
    public StakeholderResponseDTO createStakeholder(StakeholderRequestDTO dto) {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setName(dto.getName());
        stakeholder.setRole(dto.getRole());
        stakeholder.setCompany(dto.getCompany());
        stakeholder.setEmail(dto.getEmail());
        stakeholder.setPhone(dto.getPhone());
        stakeholder.setProjectId(dto.getProjectId());
        
        Stakeholder saved = stakeholderRepository.save(stakeholder);
        return convertToResponseDTO(saved);
    }

    private StakeholderResponseDTO convertToResponseDTO(Stakeholder entity) {
        StakeholderResponseDTO dto = new StakeholderResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setRole(entity.getRole());
        dto.setCompany(entity.getCompany());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setProjectId(entity.getProjectId());
        return dto;
    }

    @Transactional
    public void deleteStakeholder(UUID id) {
        stakeholderRepository.deleteById(id);
    }
}
