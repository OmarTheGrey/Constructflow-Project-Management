package com.constructflow.service;

import com.constructflow.dto.StakeholderRequestDTO;
import com.constructflow.dto.StakeholderResponseDTO;
import com.constructflow.model.Stakeholder;
import com.constructflow.repository.StakeholderRepository;
import com.constructflow.service.mapping.StakeholderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StakeholderService {
    private final StakeholderRepository stakeholderRepository;
    private final StakeholderMapper stakeholderMapper;

    public List<StakeholderResponseDTO> getStakeholdersByProject(UUID projectId) {
        return stakeholderRepository.findByProjectId(projectId).stream()
                .map(stakeholderMapper::toResponse)
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
        return stakeholderMapper.toResponse(stakeholderRepository.save(stakeholder));
    }

    @Transactional
    public void deleteStakeholder(UUID id) {
        stakeholderRepository.deleteById(id);
    }
}
