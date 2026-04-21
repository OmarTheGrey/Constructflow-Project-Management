package com.constructflow.service.strategy.prioritisation;

import com.constructflow.model.Task;

import java.util.List;

public interface PrioritisationStrategy {
    List<Task> prioritise(List<Task> tasks);
    PrioritisationKey key();
}
