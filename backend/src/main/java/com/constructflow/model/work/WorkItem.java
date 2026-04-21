package com.constructflow.model.work;

import java.math.BigDecimal;
import java.util.UUID;

public interface WorkItem {
    UUID id();
    String name();
    String status();
    double progress();
    BigDecimal actualCost();
    void accept(WorkItemVisitor visitor);
}
