package com.constructflow.service.strategy.progress;

import com.constructflow.model.Project;
import com.constructflow.model.Task;

import java.util.List;

public interface ProgressStrategy {
    /** Returns project completion in the 0..100 range. */
    double calculate(Project project, List<Task> tasks);

    ProgressModel model();
}
