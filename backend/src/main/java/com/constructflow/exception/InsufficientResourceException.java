package com.constructflow.exception;

public class InsufficientResourceException extends RuntimeException {
    public InsufficientResourceException(String resourceName, double available, double requested) {
        super("Insufficient quantity for resource '" + resourceName
                + "'. Available: " + available + ", Requested: " + requested);
    }
}
