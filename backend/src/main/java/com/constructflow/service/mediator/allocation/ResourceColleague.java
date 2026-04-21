package com.constructflow.service.mediator.allocation;

import com.constructflow.exception.ResourceNotFoundException;
import com.constructflow.model.Resource;
import com.constructflow.repository.ResourceRepository;
import com.constructflow.service.template.allocation.AllocationRequest;
import com.constructflow.service.template.allocation.AllocationValidatorRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Colleague — knows only about the mediator and its own data; never reaches
 * across to other colleagues directly.
 */
@Component
@RequiredArgsConstructor
public class ResourceColleague {

    private final ResourceRepository resourceRepository;
    private final AllocationValidatorRegistry validatorRegistry;

    /** Validate then persist the quantity deduction. Returns the updated resource. */
    public Resource reserve(AllocationCommand command) {
        Resource resource = resourceRepository.findById(command.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource", command.resourceId()));

        AllocationRequest request = new AllocationRequest(command.taskId(), command.resourceId(), command.quantity());
        validatorRegistry.forCategory(resource.getCategory()).validate(request);

        resource.setQuantity(resource.getQuantity() - command.quantity());
        return resourceRepository.save(resource);
    }

    public String categoryOf(UUID resourceId) {
        return resourceRepository.findById(resourceId).map(Resource::getCategory).orElse(null);
    }
}
