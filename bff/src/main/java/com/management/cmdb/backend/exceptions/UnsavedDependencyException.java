package com.management.cmdb.backend.exceptions;

public class UnsavedDependencyException extends RuntimeException {
    public UnsavedDependencyException() {
        super("One dependency is not saved, you must save it before save this entity");
    }

    public UnsavedDependencyException(String message) {
        super(message);
    }
}
