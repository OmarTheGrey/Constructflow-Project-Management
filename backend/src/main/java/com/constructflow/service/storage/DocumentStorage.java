package com.constructflow.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentStorage {
    StoredFile store(MultipartFile file) throws IOException;
    InputStream load(String storageKey) throws IOException;
    void delete(String storageKey) throws IOException;
}
