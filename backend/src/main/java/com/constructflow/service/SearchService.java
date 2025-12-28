package com.constructflow.service;

import com.constructflow.repository.ProjectRepository;
import com.constructflow.repository.TaskRepository;
import com.constructflow.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ResourceRepository resourceRepository;

    public Map<String, Object> globalSearch(String query) {
        Map<String, Object> results = new HashMap<>();
        results.put("projects", projectRepository.findByNameContainingIgnoreCase(query));
        results.put("tasks", taskRepository.findByNameContainingIgnoreCase(query));
        results.put("resources", resourceRepository.findByNameContainingIgnoreCase(query));
        return results;
    }
}
