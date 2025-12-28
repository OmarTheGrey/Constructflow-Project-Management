package com.constructflow.service;

import com.constructflow.model.Project;
import com.constructflow.model.Task;
import com.constructflow.repository.DailyLogRepository;
import com.constructflow.repository.ProjectRepository;
import com.constructflow.repository.ResourceRepository;
import com.constructflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
        private final ProjectRepository projectRepository;
        private final TaskRepository taskRepository;
        private final ResourceRepository resourceRepository;
        private final DailyLogRepository dailyLogRepository;

        public Map<String, Object> getDashboardStats() {
                List<Project> projects = projectRepository.findAll();
                List<Task> tasks = taskRepository.findAll();

                Map<String, Object> stats = new HashMap<>();

                long activeProjects = projects.stream()
                                .filter(p -> "Active".equalsIgnoreCase(p.getStatus()))
                                .count();

                long overdueTasks = tasks.stream()
                                .filter(t -> !"Completed".equalsIgnoreCase(t.getStatus()))
                                .filter(t -> t.getDueDate() != null
                                                && t.getDueDate().isBefore(java.time.LocalDate.now()))
                                .count();

                BigDecimal totalBudget = projects.stream()
                                .map(p -> p.getBudget() != null ? p.getBudget() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                double onSchedulePercent = projects.isEmpty() ? 0.0
                                : (double) projects.stream().filter(p -> p.getProgress() >= 50).count()
                                                / projects.size() * 100;

                stats.put("activeProjects", activeProjects);
                stats.put("totalProjects", (long) projects.size());
                stats.put("overdueTasks", overdueTasks);
                stats.put("totalBudget", totalBudget);
                stats.put("onSchedulePercentage", Math.round(onSchedulePercent));
                stats.put("totalTasks", (long) tasks.size());

                return stats;
        }

        // Advanced SQL Queries Exposure
        public Map<String, Object> getAdvancedStats() {
                Map<String, Object> stats = new HashMap<>();

                // Aggregates
                stats.put("totalBudget", projectRepository.getTotalBudget());
                stats.put("avgActualCost", projectRepository.getAverageActualCost());

                // Sub-queries
                stats.put("highValueProjects", projectRepository.findHighValueProjects());
                stats.put("expensiveTasks", taskRepository.findExpensiveTasks());

                // Complex Joins
                stats.put("activeProjectResources", resourceRepository.findResourcesInActiveProjects());
                stats.put("logsByLocation", dailyLogRepository.findLogsByProjectLocation("New York")); // Example
                                                                                                       // hardcoded,
                                                                                                       // ideally
                                                                                                       // parametric
                stats.put("tasksWithMaterial", dailyLogRepository.findTasksUsingResourceCategory("Material")); // Example

                return stats;
        }
}
