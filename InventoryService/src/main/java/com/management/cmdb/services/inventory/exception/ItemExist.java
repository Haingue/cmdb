package com.management.cmdb.services.inventory.exception;

public class ItemExist extends RuntimeException {

    public ItemExist() {
        super("Item already exists");
    }

    public ItemExist(String message) {
        super(message);
    }
}
