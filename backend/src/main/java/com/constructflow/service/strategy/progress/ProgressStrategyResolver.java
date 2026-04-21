package com.constructflow.service.strategy.progress;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ProgressStrategyResolver {

    private final Map<ProgressModel, ProgressStrategy> byModel = new EnumMap<>(ProgressModel.class);
    private final ProgressStrategy defaultStrategy;

    public ProgressStrategyResolver(List<ProgressStrategy> strategies,
                                    TaskCountProgressStrategy defaultStrategy) {
        for (ProgressStrategy s : strategies) {
            byModel.put(s.model(), s);
        }
        this.defaultStrategy = defaultStrategy;
    }

    public ProgressStrategy resolve(ProgressModel model) {
        if (model == null) return defaultStrategy;
        return byModel.getOrDefault(model, defaultStrategy);
    }
}
