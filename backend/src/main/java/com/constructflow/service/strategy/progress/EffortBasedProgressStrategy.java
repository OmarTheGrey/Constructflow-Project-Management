package com.constructflow.service.strategy.progress;

import com.constructflow.model.Project;
import com.constructflow.model.Task;
import com.constructflow.repository.WorkLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EffortBasedProgressStrategy implements ProgressStrategy {

    private final WorkLogRepository workLogRepository;

    @Override
    public double calculate(Project project, List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return 0.0;

        double totalLoggedHours = 0.0;
        double completedTaskHours = 0.0;

        for (Task t : tasks) {
            double hours = workLogRepository.findByTaskId(t.getId()).stream()
                    .mapToDouble(w -> w.getHours() == null ? 0.0 : w.getHours())
                    .sum();
            totalLoggedHours += hours;
            if ("Completed".equalsIgnoreCase(t.getStatus())) {
                completedTaskHours += hours;
            }
        }

        if (totalLoggedHours == 0.0) return 0.0;
        return completedTaskHours / totalLoggedHours * 100;
    }

    @Override public ProgressModel model() { return ProgressModel.EFFORT_BASED; }
}
