package com.constructflow.service;

import com.constructflow.dto.ResourceRequestDTO;
import com.constructflow.dto.ResourceResponseDTO;
import com.constructflow.exception.InsufficientResourceException;
import com.constructflow.exception.ResourceNotFoundException;
import com.constructflow.model.Resource;
import com.constructflow.repository.ResourceRepository;
import com.constructflow.repository.TaskAllocationRepository;
import com.constructflow.service.factory.ResourceFactory;
import com.constructflow.service.mapping.ResourceMapper;
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
    private final TaskAllocationRepository taskAllocationRepository;
    private final ResourceMapper resourceMapper;
    private final ResourceFactory resourceFactory;

    @Transactional
    public void allocateResource(UUID taskId, UUID resourceId, Double quantity) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", resourceId));

        if (resource.getQuantity() < quantity) {
            throw new InsufficientResourceException(resource.getName(), resource.getQuantity(), quantity);
        }

        resource.setQuantity(resource.getQuantity() - quantity);
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
                .orElseThrow(() -> new ResourceNotFoundException("Resource", resourceId));
        resource.setQuantity(resource.getQuantity() + quantityChange);
        resourceRepository.save(resource);
    }

    public Page<ResourceResponseDTO> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable).map(resourceMapper::toResponse);
    }

    @Transactional
    public ResourceResponseDTO createResource(ResourceRequestDTO dto) {
        return resourceMapper.toResponse(resourceRepository.save(resourceFactory.create(dto)));
    }

    @Transactional
    public ResourceResponseDTO updateResource(UUID id, ResourceRequestDTO dto) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id));
        resourceFactory.apply(resource, dto);
        return resourceMapper.toResponse(resourceRepository.save(resource));
    }

    @Transactional
    public void deleteResource(UUID id) {
        resourceRepository.deleteById(id);
    }
}
