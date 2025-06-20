package com.mywebcompanion.backendspring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String resource, String action) {
        super(String.format("Access denied: cannot %s %s", action, resource));
    }
}