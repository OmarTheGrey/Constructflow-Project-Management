package com.constructflow.service.factory.report;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ExecutiveReportFactory implements ReportArtifactFactory {

    @Override
    public ReportKind kind() { return ReportKind.EXECUTIVE; }

    @Override
    public String header(ReportContext ctx) {
        return "Executive Summary — ConstructFlow";
    }

    @Override
    public List<ReportSection> sections() {
        return List.of(
            new ReportSection() {
                public String title() { return "Project Overview"; }
                public Object render(ReportContext ctx) {
                    return Map.of(
                        "totalProjects",  ctx.get("totalProjects"),
                        "activeProjects", ctx.get("activeProjects")
                    );
                }
            },
            new ReportSection() {
                public String title() { return "Financial Summary"; }
                public Object render(ReportContext ctx) {
                    return Map.of(
                        "totalBudget",     ctx.get("totalBudget"),
                        "totalActualCost", ctx.get("totalActualCost")
                    );
                }
            },
            new ReportSection() {
                public String title() { return "Task Health"; }
                public Object render(ReportContext ctx) {
                    return Map.of(
                        "completedTasks",  ctx.get("completedTasks"),
                        "pendingTasks",    ctx.get("pendingTasks"),
                        "criticalAlerts",  ctx.get("criticalAlerts")
                    );
                }
            },
            new ReportSection() {
                public String title() { return "Recent Activity"; }
                public Object render(ReportContext ctx) {
                    return ctx.get("recentActivities");
                }
            }
        );
    }
}
