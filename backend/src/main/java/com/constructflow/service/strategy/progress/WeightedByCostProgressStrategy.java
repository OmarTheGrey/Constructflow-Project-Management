package com.constructflow.service.strategy.progress;

import com.constructflow.model.Project;
import com.constructflow.model.Task;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class WeightedByCostProgressStrategy implements ProgressStrategy {

    @Override
    public double calculate(Project project, List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return 0.0;

        BigDecimal totalCost = tasks.stream()
                .map(t -> t.getActualCost() == null ? BigDecimal.ZERO : t.getActualCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalCost.signum() == 0) {
            // Fall back to count-based when no cost data is available.
            return tasks.stream().filter(t -> "Completed".equalsIgnoreCase(t.getStatus())).count()
                    / (double) tasks.size() * 100;
        }

        BigDecimal completedCost = tasks.stream()
                .filter(t -> "Completed".equalsIgnoreCase(t.getStatus()))
                .map(t -> t.getActualCost() == null ? BigDecimal.ZERO : t.getActualCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return completedCost.doubleValue() / totalCost.doubleValue() * 100;
    }

    @Override public ProgressModel model() { return ProgressModel.WEIGHTED_BY_COST; }
}
