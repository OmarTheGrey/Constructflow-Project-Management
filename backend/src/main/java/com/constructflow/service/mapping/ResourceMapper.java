package com.constructflow.service.mapping;

import com.constructflow.dto.ResourceResponseDTO;
import com.constructflow.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {
    public ResourceResponseDTO toResponse(Resource r) {
        ResourceResponseDTO dto = new ResourceResponseDTO();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setCategory(r.getCategory());
        dto.setQuantity(r.getQuantity());
        dto.setUnit(r.getUnit());
        dto.setAllocationPercentage(r.getAllocationPercentage());
        dto.setCost(r.getCost());
        return dto;
    }
}
