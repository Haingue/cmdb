package com.management.cmdb.services.inventory.exception;

public class LinkTypeExist extends RuntimeException {

    public LinkTypeExist() {
        super("Link type already exists");
    }

    public LinkTypeExist(String message) {
        super(message);
    }
}
