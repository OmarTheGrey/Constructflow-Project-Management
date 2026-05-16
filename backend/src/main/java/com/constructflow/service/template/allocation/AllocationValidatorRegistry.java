package com.constructflow.service.template.allocation;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AllocationValidatorRegistry {

    // Category to look up when an allocation request omits one.
    private static final String DEFAULT_CATEGORY = "Material";

    private final Map<String, AbstractAllocationValidator> byCategory = new HashMap<>();
    private final AbstractAllocationValidator fallback;

    public AllocationValidatorRegistry(List<AbstractAllocationValidator> validators) {
        for (AbstractAllocationValidator v : validators) {
            byCategory.put(v.handlesCategory(), v);
        }
        AbstractAllocationValidator chosen = byCategory.get(DEFAULT_CATEGORY);
        if (chosen == null && !validators.isEmpty()) {
            chosen = validators.get(0); // arbitrary but deterministic
        }
        if (chosen == null) {
            throw new IllegalStateException(
                    "No AbstractAllocationValidator beans found - cannot pick a fallback.");
        }
        this.fallback = chosen;
    }

    public AbstractAllocationValidator forCategory(String category) {
        if (category == null) return fallback;
        return byCategory.getOrDefault(category, fallback);
    }
}
