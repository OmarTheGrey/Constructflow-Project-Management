package com.constructflow.service.strategy.prioritisation;

import com.constructflow.model.Task;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class CostDescPrioritisationStrategy implements PrioritisationStrategy {

    @Override
    public List<Task> prioritise(List<Task> tasks) {
        return tasks.stream()
                .sorted(Comparator.comparing((Task t) -> Objects.requireNonNullElse(t.getActualCost(), BigDecimal.ZERO))
                        .reversed())
                .toList();
    }

    @Override public PrioritisationKey key() { return PrioritisationKey.COST_DESC; }
}
