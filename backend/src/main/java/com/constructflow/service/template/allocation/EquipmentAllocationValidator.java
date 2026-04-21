package com.constructflow.service.template.allocation;

import com.constructflow.exception.DomainValidationException;
import com.constructflow.model.Resource;
import com.constructflow.repository.ResourceRepository;
import org.springframework.stereotype.Component;

@Component
public class EquipmentAllocationValidator extends AbstractAllocationValidator {

    public EquipmentAllocationValidator(ResourceRepository resourceRepository) {
        super(resourceRepository);
    }

    @Override
    protected void checkCategoryRules(Resource resource, AllocationRequest request) {
        // Equipment is allocated in whole units only — no half-cranes.
        double q = request.quantity();
        if (Math.floor(q) != q) {
            throw new DomainValidationException("Equipment '" + resource.getName()
                    + "' must be allocated in whole units; requested " + q);
        }
        // An equipment item cannot have more than 100% of its allocation utilised.
        Double alloc = resource.getAllocationPercentage();
        if (alloc != null && alloc >= 100.0) {
            throw new DomainValidationException("Equipment '" + resource.getName()
                    + "' is already fully allocated (" + alloc + "%)");
        }
    }

    @Override public String handlesCategory() { return "Equipment"; }
}
