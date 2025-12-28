package com.constructflow.service;

import com.constructflow.dto.ExecutiveSummaryDTO;
import com.constructflow.repository.ProjectRepository;
import com.constructflow.repository.TaskRepository;
import com.constructflow.repository.ResourceRepository;
import com.constructflow.model.Project;
import com.constructflow.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalReportService {

        private final ProjectRepository projectRepository;
        private final TaskRepository taskRepository;

        public ExecutiveSummaryDTO generateExecutiveSummary() {
                ExecutiveSummaryDTO summary = new ExecutiveSummaryDTO();

                // Project Metrics
                List<Project> allProjects = projectRepository.findAll();
                System.out.println("DEBUG: Found " + allProjects.size() + " projects.");
                for (Project p : allProjects) {
                        System.out.println("DEBUG: Project '" + p.getName() + "' - Status: '" + p.getStatus()
                                        + "', Budget: " + p.getBudget() + ", ActualCost: " + p.getActualCost());
                }

                summary.setTotalProjects(allProjects.size());
                // Try matching both "Active" and "In Progress" or generic check
                long activeCount = allProjects.stream()
                                .filter(p -> "Active".equalsIgnoreCase(p.getStatus())
                                                || "In Progress".equalsIgnoreCase(p.getStatus()))
                                .count();
                System.out.println("DEBUG: Active Projects Count: " + activeCount);
                summary.setActiveProjects(activeCount);

                // Financial Metrics
                summary.setTotalBudget(allProjects.stream()
                                .mapToDouble(p -> p.getBudget() != null ? p.getBudget().doubleValue() : 0.0)
                                .sum());
                summary.setTotalActualCost(
                                allProjects.stream()
                                                .mapToDouble(p -> p.getActualCost() != null
                                                                ? p.getActualCost().doubleValue()
                                                                : 0.0)
                                                .sum());

                // Task Metrics
                List<Task> allTasks = taskRepository.findAll();
                summary.setCompletedTasks(
                                allTasks.stream().filter(t -> "Completed".equalsIgnoreCase(t.getStatus())).count());
                summary.setPendingTasks(
                                allTasks.stream().filter(t -> !"Completed".equalsIgnoreCase(t.getStatus())).count());

                // Alerts: Tasks overdue
                long overdueTasks = allTasks.stream()
                                .filter(t -> t.getDueDate() != null
                                                && t.getDueDate().isBefore(java.time.LocalDate.now())
                                                && !"Completed".equalsIgnoreCase(t.getStatus()))
                                .count();
                summary.setCriticalAlerts(overdueTasks);

                // Recent Activities: Last 5 modified tasks
                List<String> activities = allTasks.stream()
                                .sorted((t1, t2) -> {
                                        if (t1.getLastModifiedAt() == null)
                                                return 1;
                                        if (t2.getLastModifiedAt() == null)
                                                return -1;
                                        return t2.getLastModifiedAt().compareTo(t1.getLastModifiedAt());
                                })
                                .limit(5)
                                .map(t -> "Task updated: " + t.getName() + " (" + t.getStatus() + ")")
                                .collect(java.util.stream.Collectors.toList());
                summary.setRecentActivities(activities);

                System.out.println("Generated Report Summary: " + summary);
                return summary;
        }
}
