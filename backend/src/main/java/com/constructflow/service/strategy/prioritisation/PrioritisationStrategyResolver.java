package com.constructflow.service.strategy.prioritisation;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PrioritisationStrategyResolver {

    private final Map<PrioritisationKey, PrioritisationStrategy> byKey = new EnumMap<>(PrioritisationKey.class);
    private final PrioritisationStrategy defaultStrategy;

    public PrioritisationStrategyResolver(List<PrioritisationStrategy> strategies,
                                          DueDatePrioritisationStrategy defaultStrategy) {
        for (PrioritisationStrategy s : strategies) {
            byKey.put(s.key(), s);
        }
        this.defaultStrategy = defaultStrategy;
    }

    public PrioritisationStrategy resolve(PrioritisationKey key) {
        if (key == null) return defaultStrategy;
        return byKey.getOrDefault(key, defaultStrategy);
    }
}
