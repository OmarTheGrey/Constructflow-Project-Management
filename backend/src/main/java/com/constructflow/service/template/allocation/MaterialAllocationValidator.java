package com.constructflow.service.template.allocation;

import com.constructflow.exception.DomainValidationException;
import com.constructflow.model.Resource;
import com.constructflow.repository.ResourceRepository;
import org.springframework.stereotype.Component;

@Component
public class MaterialAllocationValidator extends AbstractAllocationValidator {

    // Materials below this threshold after allocation are flagged as under-stocked.
    private static final double MIN_REORDER_BUFFER = 5.0;

    public MaterialAllocationValidator(ResourceRepository resourceRepository) {
        super(resourceRepository);
    }

    @Override
    protected void checkCategoryRules(Resource resource, AllocationRequest request) {
        double remaining = resource.getQuantity() - request.quantity();
        if (remaining < MIN_REORDER_BUFFER) {
            throw new DomainValidationException("Material '" + resource.getName()
                    + "' would drop below reorder buffer (" + MIN_REORDER_BUFFER
                    + " " + resource.getUnit() + ") after this allocation");
        }
    }

    @Override public String handlesCategory() { return "Material"; }
}
