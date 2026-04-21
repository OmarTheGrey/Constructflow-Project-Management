package com.constructflow.model.work;

import com.constructflow.model.Task;

import java.math.BigDecimal;
import java.util.UUID;

public final class LeafTask implements WorkItem {

    private final Task task;

    public LeafTask(Task task) { this.task = task; }

    @Override public UUID id()               { return task.getId(); }
    @Override public String name()           { return task.getName(); }
    @Override public String status()         { return task.getStatus(); }
    @Override public BigDecimal actualCost() { return task.getActualCost(); }

    @Override
    public double progress() {
        return "Completed".equalsIgnoreCase(task.getStatus()) ? 1.0 : 0.0;
    }

    @Override
    public void accept(WorkItemVisitor visitor) { visitor.visitLeaf(this); }

    public Task getTask() { return task; }
}
