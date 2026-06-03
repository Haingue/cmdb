package com.management.cmdb.services.inventory.exception;

public class LinkTypeNotExist extends RuntimeException {

    public LinkTypeNotExist() {
        super("Link type does not exist");
    }

    public LinkTypeNotExist(String message) {
        super(message);
    }
}
