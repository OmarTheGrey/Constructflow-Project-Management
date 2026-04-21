package com.constructflow.service.mapping;

import com.constructflow.dto.StakeholderResponseDTO;
import com.constructflow.model.Stakeholder;
import org.springframework.stereotype.Component;

@Component
public class StakeholderMapper {
    public StakeholderResponseDTO toResponse(Stakeholder entity) {
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
}
