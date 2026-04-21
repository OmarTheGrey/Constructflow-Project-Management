package com.constructflow.service;

import com.constructflow.dto.ExecutiveSummaryDTO;
import com.constructflow.model.Project;
import com.constructflow.model.Task;
import com.constructflow.repository.TaskRepository;
import com.constructflow.service.factory.report.ReportArtifactFactory;
import com.constructflow.service.factory.report.ReportContext;
import com.constructflow.service.factory.report.ReportKind;
import com.constructflow.service.iteration.ProjectScanner;
import com.constructflow.service.iteration.ProjectTaskTreeIterator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final List<ReportArtifactFactory> factories;
    private final ProjectScanner projectScanner;
    private final TaskRepository taskRepository;
    private final GlobalReportService globalReportService;

    public Map<String, Object> build(ReportKind kind) {
        ReportArtifactFactory factory = factories.stream()
                .filter(f -> f.kind() == kind)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No factory for kind: " + kind));

        ReportContext ctx = buildContext();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("header", factory.header(ctx));
        List<Map<String, Object>> sections = new ArrayList<>();
        for (var section : factory.sections()) {
            sections.add(Map.of(section.title(), section.render(ctx)));
        }
        result.put("sections", sections);
        return result;
    }

    public ExecutiveSummaryDTO executiveSummary() {
        return globalReportService.generateExecutiveSummary();
    }

    private ReportContext buildContext() {
        ReportContext ctx = new ReportContext();

        long totalProjects = 0, activeProjects = 0;
        double totalBudget = 0.0, totalActualCost = 0.0;
        for (Project p : projectScanner) {
            totalProjects++;
            if ("Active".equalsIgnoreCase(p.getStatus()) || "In Progress".equalsIgnoreCase(p.getStatus())) activeProjects++;
            if (p.getBudget() != null)     totalBudget     += p.getBudget().doubleValue();
            if (p.getActualCost() != null) totalActualCost += p.getActualCost().doubleValue();
        }

        long completedTasks = 0, pendingTasks = 0, overdueTasks = 0;
        List<Task> allTasks = new ArrayList<>();
        ProjectTaskTreeIterator taskIter = new ProjectTaskTreeIterator(projectScanner.iterator(), taskRepository);
        while (taskIter.hasNext()) {
            Task t = taskIter.next();
            allTasks.add(t);
            if ("Completed".equalsIgnoreCase(t.getStatus())) completedTasks++;
            else {
                pendingTasks++;
                if (t.getDueDate() != null && t.getDueDate().isBefore(java.time.LocalDate.now())) overdueTasks++;
            }
        }

        List<String> recentActivities = allTasks.stream()
                .filter(t -> t.getLastModifiedAt() != null)
                .sorted(Comparator.comparing(Task::getLastModifiedAt).reversed())
                .limit(5)
                .map(t -> "Task updated: " + t.getName() + " (" + t.getStatus() + ")")
                .toList();

        ctx.put("totalProjects",    totalProjects);
        ctx.put("activeProjects",   activeProjects);
        ctx.put("totalBudget",      totalBudget);
        ctx.put("totalActualCost",  totalActualCost);
        ctx.put("completedTasks",   completedTasks);
        ctx.put("pendingTasks",     pendingTasks);
        ctx.put("criticalAlerts",   overdueTasks);
        ctx.put("recentActivities", recentActivities);
        return ctx;
    }
}
