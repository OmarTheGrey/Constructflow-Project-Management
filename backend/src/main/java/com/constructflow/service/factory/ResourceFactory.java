package com.constructflow.service.factory;

import com.constructflow.dto.ResourceRequestDTO;
import com.constructflow.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceFactory implements EntityFactory<Resource, ResourceRequestDTO> {

    @Override
    public Resource create(ResourceRequestDTO dto) {
        Resource resource = new Resource();
        apply(resource, dto);
        if (resource.getAllocationPercentage() == null) resource.setAllocationPercentage(0.0);
        if (resource.getCost() == null)                resource.setCost(0.0);
        return resource;
    }

    @Override
    public void apply(Resource resource, ResourceRequestDTO dto) {
        if (dto.getName() != null)                 resource.setName(dto.getName());
        if (dto.getCategory() != null)             resource.setCategory(dto.getCategory());
        if (dto.getQuantity() != null)             resource.setQuantity(dto.getQuantity());
        if (dto.getUnit() != null)                 resource.setUnit(dto.getUnit());
        if (dto.getAllocationPercentage() != null)  resource.setAllocationPercentage(dto.getAllocationPercentage());
        if (dto.getCost() != null)                 resource.setCost(dto.getCost());
    }
}
