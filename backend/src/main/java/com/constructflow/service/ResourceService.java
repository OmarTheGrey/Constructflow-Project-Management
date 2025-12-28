package com.constructflow.service;

import com.constructflow.dto.ResourceRequestDTO;
import com.constructflow.dto.ResourceResponseDTO;
import com.constructflow.model.Resource;
import com.constructflow.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final com.constructflow.repository.TaskAllocationRepository taskAllocationRepository;

    @Transactional
    public void allocateResource(UUID taskId, UUID resourceId, Double quantity) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        if (resource.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient resource quantity. Available: " + resource.getQuantity());
        }

        // Deduct from available quantity
        resource.setQuantity(resource.getQuantity() - quantity);

        // Update allocation percentage/allocated amount if we tracked it explicitly.
        // For now, let's assume 'quantity' is free stock.
        // We can track totalAllocated in Resource if we add a field, or calculate it.
        // Let's add to allocated if we have a field for it, or just use quantity as
        // available.
        // The implementation plan mentioned "Subtract from Resource.quantity, add to
        // Resource.allocated".
        // Resource entity currently has allocationPercentage, but not
        // 'allocatedQuantity'.
        // Let's rely on quantity being 'available'.

        resourceRepository.save(resource);

        com.constructflow.model.TaskAllocation allocation = new com.constructflow.model.TaskAllocation();
        allocation.setTaskId(taskId);
        allocation.setResourceId(resourceId);
        allocation.setQuantityAllocated(quantity);
        allocation.setAllocatedAt(java.time.LocalDateTime.now());
        taskAllocationRepository.save(allocation);
    }

    @Transactional
    public void updateInventory(UUID resourceId, Double quantityChange, String reason) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        resource.setQuantity(resource.getQuantity() + quantityChange);
        resourceRepository.save(resource);
        // We could log the 'reason' in a separate Audit Log table here.
        System.out.println(
                "Inventory Update: " + resource.getName() + " change: " + quantityChange + " Reason: " + reason);
    }

    public Page<ResourceResponseDTO> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional
    public ResourceResponseDTO createResource(ResourceRequestDTO dto) {
        Resource resource = new Resource();
        resource.setName(dto.getName());
        resource.setCategory(dto.getCategory());
        resource.setQuantity(dto.getQuantity());
        resource.setUnit(dto.getUnit());
        resource.setAllocationPercentage(dto.getAllocationPercentage() != null ? dto.getAllocationPercentage() : 0.0);
        resource.setCost(dto.getCost() != null ? dto.getCost() : 0.0); // Set cost

        return mapToResponseDTO(resourceRepository.save(resource));
    }

    @Transactional
    public ResourceResponseDTO updateResource(UUID id, ResourceRequestDTO dto) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        resource.setName(dto.getName());
        resource.setCategory(dto.getCategory());
        resource.setQuantity(dto.getQuantity());
        resource.setUnit(dto.getUnit());
        resource.setAllocationPercentage(dto.getAllocationPercentage());
        resource.setCost(dto.getCost() != null ? dto.getCost() : 0.0); // Set cost

        return mapToResponseDTO(resourceRepository.save(resource));
    }

    @Transactional
    public void deleteResource(UUID id) {
        resourceRepository.deleteById(id);
    }

    private ResourceResponseDTO mapToResponseDTO(Resource r) {
        ResourceResponseDTO dto = new ResourceResponseDTO();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setCategory(r.getCategory());
        dto.setQuantity(r.getQuantity());
        dto.setUnit(r.getUnit());
        dto.setAllocationPercentage(r.getAllocationPercentage());
        dto.setCost(r.getCost()); // Map cost
        return dto;
    }
}
