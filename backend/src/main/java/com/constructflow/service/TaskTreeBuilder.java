package com.constructflow.service;

import com.constructflow.model.Task;
import com.constructflow.model.work.CompositeTask;
import com.constructflow.model.work.LeafTask;
import com.constructflow.model.work.WorkItem;
import com.constructflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TaskTreeBuilder {

    private final TaskRepository taskRepository;

    public WorkItem buildForProject(UUID projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        Map<UUID, CompositeTask> composites = new HashMap<>();
        Map<UUID, LeafTask> leaves = new HashMap<>();

        for (Task t : tasks) {
            if (hasChildren(t.getId(), tasks)) {
                composites.put(t.getId(), new CompositeTask(t));
            } else {
                leaves.put(t.getId(), new LeafTask(t));
            }
        }

        // Virtual root composite for the whole project
        Task virtualRoot = new Task();
        virtualRoot.setName("Project Root");
        virtualRoot.setStatus("In Progress");
        CompositeTask root = new CompositeTask(virtualRoot);

        for (Task t : tasks) {
            WorkItem item = composites.containsKey(t.getId())
                    ? composites.get(t.getId())
                    : leaves.get(t.getId());

            if (t.getParentTaskId() == null) {
                root.add(item);
            } else {
                CompositeTask parent = composites.get(t.getParentTaskId());
                if (parent != null) {
                    parent.add(item);
                } else {
                    root.add(item);
                }
            }
        }

        return root;
    }

    private boolean hasChildren(UUID taskId, List<Task> tasks) {
        return tasks.stream().anyMatch(t -> taskId.equals(t.getParentTaskId()));
    }
}
