package com.constructflow.service.template.allocation;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AllocationValidatorRegistry {

    private final Map<String, AbstractAllocationValidator> byCategory = new HashMap<>();
    private final AbstractAllocationValidator fallback;

    public AllocationValidatorRegistry(List<AbstractAllocationValidator> validators,
                                       MaterialAllocationValidator fallback) {
        for (AbstractAllocationValidator v : validators) {
            byCategory.put(v.handlesCategory(), v);
        }
        this.fallback = fallback; // default to the strictest when category is missing
    }

    public AbstractAllocationValidator forCategory(String category) {
        if (category == null) return fallback;
        return byCategory.getOrDefault(category, fallback);
    }
}
