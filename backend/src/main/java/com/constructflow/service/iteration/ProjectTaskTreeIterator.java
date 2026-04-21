package com.constructflow.service.iteration;

import com.constructflow.model.Project;
import com.constructflow.model.Task;
import com.constructflow.repository.TaskRepository;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ProjectTaskTreeIterator implements Iterator<Task> {

    private final Iterator<Project> projects;
    private final TaskRepository taskRepository;
    private Iterator<Task> currentTasks = Collections.emptyIterator();

    public ProjectTaskTreeIterator(Iterator<Project> projects, TaskRepository taskRepository) {
        this.projects = projects;
        this.taskRepository = taskRepository;
    }

    @Override
    public boolean hasNext() {
        while (!currentTasks.hasNext() && projects.hasNext()) {
            currentTasks = taskRepository.findByProjectId(projects.next().getId()).iterator();
        }
        return currentTasks.hasNext();
    }

    @Override
    public Task next() {
        if (!hasNext()) throw new NoSuchElementException();
        return currentTasks.next();
    }
}
