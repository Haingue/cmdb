package com.management.cmdb.backend.exceptions;

public class AdapterException extends RuntimeException {
    public AdapterException() {
        super("Error from adapter");
    }
    public AdapterException(String message) {
        super(message);
    }
}
