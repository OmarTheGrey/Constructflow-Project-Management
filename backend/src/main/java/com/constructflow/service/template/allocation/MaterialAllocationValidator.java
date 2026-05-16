package com.constructflow.service.template.allocation;

import com.constructflow.model.Resource;
import com.constructflow.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MaterialAllocationValidator extends AbstractAllocationValidator {

    private static final Logger log = LoggerFactory.getLogger(MaterialAllocationValidator.class);

    // Materials below this threshold after allocation are flagged as under-stocked.
    private static final double MIN_REORDER_BUFFER = 5.0;

    public MaterialAllocationValidator(ResourceRepository resourceRepository) {
        super(resourceRepository);
    }

    @Override
    protected void checkCategoryRules(Resource resource, AllocationRequest request) {
        double remaining = resource.getQuantity() - request.quantity();
        if (remaining < MIN_REORDER_BUFFER) {
            log.warn("Material '{}' will be below reorder buffer ({} {}) after this allocation "
                            + "(remaining={}). Allocation allowed; consider procurement.",
                    resource.getName(), MIN_REORDER_BUFFER, resource.getUnit(), remaining);
        }
    }

    @Override public String handlesCategory() { return "Material"; }
}
