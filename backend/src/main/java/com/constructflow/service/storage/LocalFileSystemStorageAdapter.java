package com.constructflow.service.storage;

import com.constructflow.config.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocalFileSystemStorageAdapter implements DocumentStorage {

    private final StorageProperties storageProperties;

    @Override
    public StoredFile store(MultipartFile file) throws IOException {
        Path uploadRoot = Paths.get(storageProperties.getLocalPath());
        if (!Files.exists(uploadRoot)) {
            Files.createDirectories(uploadRoot);
        }
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), uploadRoot.resolve(key));
        return new StoredFile(key, file.getSize());
    }

    @Override
    public InputStream load(String storageKey) throws IOException {
        Path filePath = Paths.get(storageProperties.getLocalPath()).resolve(storageKey);
        return Files.newInputStream(filePath);
    }

    @Override
    public void delete(String storageKey) throws IOException {
        Path filePath = Paths.get(storageProperties.getLocalPath()).resolve(storageKey);
        Files.deleteIfExists(filePath);
    }
}
