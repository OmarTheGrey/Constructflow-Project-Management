package com.constructflow.service.strategy.progress;

import com.constructflow.model.Project;
import com.constructflow.model.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class MilestoneBasedProgressStrategy implements ProgressStrategy {

    @Override
    public double calculate(Project project, List<Task> tasks) {
        if (project.getMilestones() == null || project.getMilestones().isEmpty()) {
            return 0.0;
        }

        long hit = project.getMilestones().stream()
                .filter(m -> isHit(m, tasks))
                .count();

        return (double) hit / project.getMilestones().size() * 100;
    }

    private boolean isHit(String milestone, List<Task> tasks) {
        if (milestone == null || tasks == null) return false;
        String needle = milestone.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(t -> "Completed".equalsIgnoreCase(t.getStatus()))
                .anyMatch(t -> t.getName() != null
                        && t.getName().toLowerCase(Locale.ROOT).contains(needle));
    }

    @Override public ProgressModel model() { return ProgressModel.MILESTONE_BASED; }
}
