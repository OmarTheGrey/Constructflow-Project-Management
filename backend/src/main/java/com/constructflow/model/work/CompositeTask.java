package com.constructflow.model.work;

import com.constructflow.model.Task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class CompositeTask implements WorkItem {

    private final Task self;
    private final List<WorkItem> children = new ArrayList<>();

    public CompositeTask(Task self) { this.self = self; }

    public void add(WorkItem item) { children.add(item); }

    public List<WorkItem> getChildren() { return Collections.unmodifiableList(children); }

    @Override public UUID id()   { return self.getId(); }
    @Override public String name() { return self.getName(); }

    @Override
    public String status() {
        return progress() >= 1.0 ? "Completed" : "In Progress";
    }

    @Override
    public double progress() {
        if (children.isEmpty()) return 0.0;
        return children.stream()
                .mapToDouble(WorkItem::progress)
                .average()
                .orElse(0.0);
    }

    @Override
    public BigDecimal actualCost() {
        return children.stream()
                .map(WorkItem::actualCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void accept(WorkItemVisitor visitor) {
        visitor.visitComposite(this);
        children.forEach(c -> c.accept(visitor));
    }
}
