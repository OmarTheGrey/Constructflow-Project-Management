package com.constructflow.service;

import com.constructflow.dto.ResourceRequestDTO;
import com.constructflow.dto.ResourceResponseDTO;
import com.constructflow.exception.ResourceNotFoundException;
import com.constructflow.model.Resource;
import com.constructflow.repository.ResourceRepository;
import com.constructflow.service.factory.ResourceFactory;
import com.constructflow.service.mapping.ResourceMapper;
import com.constructflow.service.mediator.allocation.AllocationCommand;
import com.constructflow.service.mediator.allocation.AllocationMediator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ResourceFactory resourceFactory;
    private final AllocationMediator allocationMediator;

    @Transactional
    public void allocateResource(UUID taskId, UUID resourceId, Double quantity) {
        allocationMediator.allocate(new AllocationCommand(taskId, resourceId, quantity));
    }

    @Transactional
    public void updateInventory(UUID resourceId, Double quantityChange, String reason) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", resourceId));
        resource.setQuantity(resource.getQuantity() + quantityChange);
        resourceRepository.save(resource);
        log.info("Inventory updated: resource='{}' change={} reason='{}'", resource.getName(), quantityChange, reason);
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
