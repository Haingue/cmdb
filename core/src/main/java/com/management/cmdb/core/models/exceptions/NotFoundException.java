package com.management.cmdb.core.models.exceptions;

public class NotFoundException extends CoreException {
    public NotFoundException(Object identifier) {
        super(String.format("Object not found: %s", identifier));
    }
}
