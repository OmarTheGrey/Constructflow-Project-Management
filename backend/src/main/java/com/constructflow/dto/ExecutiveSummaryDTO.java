package com.constructflow.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExecutiveSummaryDTO {
    private long totalProjects;
    private long activeProjects;
    private double totalBudget;
    private double totalActualCost;
    private long completedTasks;
    private long pendingTasks;
    private long criticalAlerts;
    private List<String> recentActivities;
}
