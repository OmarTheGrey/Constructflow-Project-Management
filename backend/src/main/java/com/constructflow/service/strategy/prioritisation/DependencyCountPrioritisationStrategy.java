package com.constructflow.service.strategy.prioritisation;

import com.constructflow.model.Task;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class DependencyCountPrioritisationStrategy implements PrioritisationStrategy {

    @Override
    public List<Task> prioritise(List<Task> tasks) {
        return tasks.stream()
                .sorted(Comparator.comparingInt((Task t) -> t.getDependencies() == null ? 0 : t.getDependencies().size())
                        .reversed())
                .toList();
    }

    @Override public PrioritisationKey key() { return PrioritisationKey.DEPENDENCIES; }
}
