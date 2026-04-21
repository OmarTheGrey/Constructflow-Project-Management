package com.constructflow.service.template.allocation;

import com.constructflow.exception.DomainValidationException;
import com.constructflow.exception.InsufficientResourceException;
import com.constructflow.exception.ResourceNotFoundException;
import com.constructflow.model.Resource;
import com.constructflow.repository.ResourceRepository;

public abstract class AbstractAllocationValidator {

    protected final ResourceRepository resourceRepository;

    protected AbstractAllocationValidator(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    /**
     * Template method — fixed validation skeleton. Subclasses plug in the category-specific rule.
     */
    public final Resource validate(AllocationRequest request) {
        checkBasicInputs(request);
        Resource resource = resolveResource(request);
        checkAvailability(resource, request);
        checkCategoryRules(resource, request); // subclass-specific
        checkProjectBudget(resource, request); // hook — default no-op
        return resource;
    }

    protected void checkBasicInputs(AllocationRequest request) {
        if (request.taskId() == null || request.resourceId() == null) {
            throw new DomainValidationException("taskId and resourceId are required");
        }
        if (request.quantity() <= 0) {
            throw new DomainValidationException("Allocation quantity must be positive");
        }
    }

    protected Resource resolveResource(AllocationRequest request) {
        return resourceRepository.findById(request.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource", request.resourceId()));
    }

    protected void checkAvailability(Resource resource, AllocationRequest request) {
        if (resource.getQuantity() == null || resource.getQuantity() < request.quantity()) {
            throw new InsufficientResourceException(
                    resource.getName(),
                    resource.getQuantity() == null ? 0.0 : resource.getQuantity(),
                    request.quantity());
        }
    }

    /** Subclasses must define their own category-specific validation. */
    protected abstract void checkCategoryRules(Resource resource, AllocationRequest request);

    /** Hook — subclasses may override to add budget constraints. */
    protected void checkProjectBudget(Resource resource, AllocationRequest request) {
        // no-op by default
    }

    /** The category this validator handles. */
    public abstract String handlesCategory();
}
