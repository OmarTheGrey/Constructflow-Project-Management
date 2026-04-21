package com.constructflow.service;

import com.constructflow.config.AppProperties;
import com.constructflow.model.Project;
import com.constructflow.model.Task;
import com.constructflow.repository.DailyLogRepository;
import com.constructflow.repository.ProjectRepository;
import com.constructflow.repository.ResourceRepository;
import com.constructflow.repository.TaskRepository;
import com.constructflow.service.iteration.PagedRepositoryIterator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ResourceRepository resourceRepository;
    private final DailyLogRepository dailyLogRepository;
    private final AppProperties appProperties;

    public Map<String, Object> getDashboardStats() {
        String activeStatus    = appProperties.getStatus().getActive();
        String completedStatus = appProperties.getStatus().getCompleted();

        Map<String, Object> stats = new HashMap<>();

        long totalProjects = 0, activeProjects = 0, onScheduleCount = 0;
        BigDecimal totalBudget = BigDecimal.ZERO;

        PagedRepositoryIterator<Project> projectIter =
                new PagedRepositoryIterator<>(projectRepository::findAll, 100);
        while (projectIter.hasNext()) {
            Project p = projectIter.next();
            totalProjects++;
            if (activeStatus.equalsIgnoreCase(p.getStatus())) activeProjects++;
            if (p.getBudget() != null) totalBudget = totalBudget.add(p.getBudget());
            if (p.getProgress() != null && p.getProgress() >= 50) onScheduleCount++;
        }

        long totalTasks = 0, overdueTasks = 0;
        PagedRepositoryIterator<Task> taskIter =
                new PagedRepositoryIterator<>(taskRepository::findAll, 100);
        while (taskIter.hasNext()) {
            Task t = taskIter.next();
            totalTasks++;
            if (!completedStatus.equalsIgnoreCase(t.getStatus())
                    && t.getDueDate() != null
                    && t.getDueDate().isBefore(java.time.LocalDate.now())) {
                overdueTasks++;
            }
        }

        double onSchedulePct = totalProjects == 0 ? 0.0 : (double) onScheduleCount / totalProjects * 100;

        stats.put("activeProjects",       activeProjects);
        stats.put("totalProjects",        totalProjects);
        stats.put("overdueTasks",         overdueTasks);
        stats.put("totalBudget",          totalBudget);
        stats.put("onSchedulePercentage", Math.round(onSchedulePct));
        stats.put("totalTasks",           totalTasks);

        return stats;
    }

    public Map<String, Object> getAdvancedStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBudget",          projectRepository.getTotalBudget());
        stats.put("avgActualCost",        projectRepository.getAverageActualCost());
        stats.put("highValueProjects",    projectRepository.findHighValueProjects());
        stats.put("expensiveTasks",       taskRepository.findExpensiveTasks());
        stats.put("activeProjectResources", resourceRepository.findResourcesInActiveProjects());
        stats.put("logsByLocation",       dailyLogRepository.findLogsByProjectLocation("New York"));
        stats.put("tasksWithMaterial",    dailyLogRepository.findTasksUsingResourceCategory("Material"));
        return stats;
    }
}
