package com.constructflow.service;

import com.constructflow.config.AppProperties;
import com.constructflow.dto.ExecutiveSummaryDTO;
import com.constructflow.model.Project;
import com.constructflow.model.Task;
import com.constructflow.repository.TaskRepository;
import com.constructflow.service.iteration.ProjectScanner;
import com.constructflow.service.iteration.ProjectTaskTreeIterator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalReportService {

    private final ProjectScanner projectScanner;
    private final TaskRepository taskRepository;
    private final AppProperties appProperties;

    public ExecutiveSummaryDTO generateExecutiveSummary() {
        String activeStatus    = appProperties.getStatus().getActive();
        String inProgressStatus = appProperties.getStatus().getInProgress();
        String completedStatus = appProperties.getStatus().getCompleted();

        ExecutiveSummaryDTO summary = new ExecutiveSummaryDTO();

        long totalProjects = 0, activeProjects = 0;
        double totalBudget = 0.0, totalActualCost = 0.0;

        for (Project p : projectScanner) {
            totalProjects++;
            if (activeStatus.equalsIgnoreCase(p.getStatus())
                    || inProgressStatus.equalsIgnoreCase(p.getStatus())) {
                activeProjects++;
            }
            if (p.getBudget() != null)     totalBudget     += p.getBudget().doubleValue();
            if (p.getActualCost() != null) totalActualCost += p.getActualCost().doubleValue();
        }

        summary.setTotalProjects(totalProjects);
        summary.setActiveProjects(activeProjects);
        summary.setTotalBudget(totalBudget);
        summary.setTotalActualCost(totalActualCost);

        long completedTasks = 0, pendingTasks = 0, overdueTasks = 0;
        List<Task> recentCandidates = new ArrayList<>();

        ProjectTaskTreeIterator taskIterator = new ProjectTaskTreeIterator(
                projectScanner.iterator(), taskRepository);

        while (taskIterator.hasNext()) {
            Task t = taskIterator.next();
            if (completedStatus.equalsIgnoreCase(t.getStatus())) {
                completedTasks++;
            } else {
                pendingTasks++;
                if (t.getDueDate() != null && t.getDueDate().isBefore(java.time.LocalDate.now())) {
                    overdueTasks++;
                }
            }
            recentCandidates.add(t);
        }

        summary.setCompletedTasks(completedTasks);
        summary.setPendingTasks(pendingTasks);
        summary.setCriticalAlerts(overdueTasks);

        List<String> activities = recentCandidates.stream()
                .filter(t -> t.getLastModifiedAt() != null)
                .sorted(Comparator.comparing(Task::getLastModifiedAt).reversed())
                .limit(5)
                .map(t -> "Task updated: " + t.getName() + " (" + t.getStatus() + ")")
                .toList();
        summary.setRecentActivities(activities);

        return summary;
    }
}
