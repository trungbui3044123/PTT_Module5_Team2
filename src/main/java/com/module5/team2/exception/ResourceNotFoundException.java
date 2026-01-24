package com.module5.team2.exception;

public class ResourceNotFoundException extends BusinessException{
public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resource, String identifier) {
        super(resource + " not found: " + identifier, "RESOURCE_NOT_FOUND");
    }
}
