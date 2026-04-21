package com.constructflow.service.iteration;

import com.constructflow.model.Project;
import com.constructflow.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class ProjectScanner implements Iterable<Project> {

    private static final int PAGE_SIZE = 100;
    private final ProjectRepository projectRepository;

    @Override
    public Iterator<Project> iterator() {
        return new PagedRepositoryIterator<>(projectRepository::findAll, PAGE_SIZE);
    }
}
