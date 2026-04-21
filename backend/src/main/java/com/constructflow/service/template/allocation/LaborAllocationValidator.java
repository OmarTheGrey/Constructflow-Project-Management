package com.constructflow.service.template.allocation;

import com.constructflow.exception.DomainValidationException;
import com.constructflow.model.Resource;
import com.constructflow.repository.ResourceRepository;
import org.springframework.stereotype.Component;

@Component
public class LaborAllocationValidator extends AbstractAllocationValidator {

    // Hard cap on daily labor hours per single allocation.
    private static final double MAX_HOURS_PER_ALLOCATION = 12.0;

    public LaborAllocationValidator(ResourceRepository resourceRepository) {
        super(resourceRepository);
    }

    @Override
    protected void checkCategoryRules(Resource resource, AllocationRequest request) {
        // Labor quantity is hours. We reject any single allocation over the daily cap to
        // discourage illegal overtime bookings.
        if (request.quantity() > MAX_HOURS_PER_ALLOCATION) {
            throw new DomainValidationException("Labor allocation for '" + resource.getName()
                    + "' exceeds the " + MAX_HOURS_PER_ALLOCATION
                    + "-hour per-allocation cap (requested " + request.quantity() + ")");
        }
    }

    @Override public String handlesCategory() { return "Labor"; }
}
