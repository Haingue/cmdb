package com.management.cmdb.services.inventory.exception;

public class LinkTypeNotValid extends RuntimeException {

    public LinkTypeNotValid() {
        super("Link type is invalid");
    }

    public LinkTypeNotValid(String message) {
        super(message);
    }
}
