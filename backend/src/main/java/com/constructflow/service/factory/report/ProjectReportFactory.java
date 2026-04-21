package com.constructflow.service.factory.report;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ProjectReportFactory implements ReportArtifactFactory {

    @Override
    public ReportKind kind() { return ReportKind.PROJECT; }

    @Override
    public String header(ReportContext ctx) {
        return "Project Status Report — ConstructFlow";
    }

    @Override
    public List<ReportSection> sections() {
        return List.of(
            new ReportSection() {
                public String title() { return "Project Counts"; }
                public Object render(ReportContext ctx) {
                    return Map.of(
                        "totalProjects",  ctx.get("totalProjects"),
                        "activeProjects", ctx.get("activeProjects")
                    );
                }
            },
            new ReportSection() {
                public String title() { return "Task Progress"; }
                public Object render(ReportContext ctx) {
                    return Map.of(
                        "completedTasks", ctx.get("completedTasks"),
                        "pendingTasks",   ctx.get("pendingTasks")
                    );
                }
            },
            new ReportSection() {
                public String title() { return "Overdue Alerts"; }
                public Object render(ReportContext ctx) {
                    return Map.of("criticalAlerts", ctx.get("criticalAlerts"));
                }
            }
        );
    }
}
