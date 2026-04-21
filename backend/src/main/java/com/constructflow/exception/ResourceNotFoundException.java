package com.constructflow.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entityName, UUID id) {
        super(entityName + " not found with id: " + id);
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
