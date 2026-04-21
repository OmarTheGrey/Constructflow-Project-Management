package com.constructflow.service.strategy.prioritisation;

import com.constructflow.model.Task;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Component
public class RiskWeightedPrioritisationStrategy implements PrioritisationStrategy {

    @Override
    public List<Task> prioritise(List<Task> tasks) {
        return tasks.stream()
                .sorted(Comparator.comparingDouble(this::riskScore).reversed())
                .toList();
    }

    /**
     * Combines urgency, cost exposure, and dependency fan-out into a single score.
     * Higher score = higher risk = earlier in the output list.
     */
    private double riskScore(Task t) {
        double urgency = urgencyScore(t.getDueDate());
        double cost    = t.getActualCost() == null ? 0.0 : t.getActualCost().doubleValue();
        int deps       = t.getDependencies() == null ? 0 : t.getDependencies().size();
        double priorityMultiplier = "Critical".equalsIgnoreCase(t.getPriority()) ? 2.0 : 1.0;

        return priorityMultiplier * (urgency * 10 + Math.log10(Math.max(cost, 1)) + deps * 2.0);
    }

    private double urgencyScore(LocalDate dueDate) {
        if (dueDate == null) return 0.0;
        long daysToDue = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        if (daysToDue < 0)  return 10.0;             // overdue
        if (daysToDue == 0) return 8.0;              // due today
        return Math.max(0.0, 7.0 - daysToDue * 0.5); // fades out over two weeks
    }

    @Override public PrioritisationKey key() { return PrioritisationKey.RISK; }
}
