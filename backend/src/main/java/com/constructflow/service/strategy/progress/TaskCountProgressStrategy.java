package com.constructflow.service.strategy.progress;

import com.constructflow.model.Project;
import com.constructflow.model.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskCountProgressStrategy implements ProgressStrategy {

    @Override
    public double calculate(Project project, List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return 0.0;
        long completed = tasks.stream()
                .filter(t -> "Completed".equalsIgnoreCase(t.getStatus()))
                .count();
        return (double) completed / tasks.size() * 100;
    }

    @Override public ProgressModel model() { return ProgressModel.TASK_COUNT; }
}
