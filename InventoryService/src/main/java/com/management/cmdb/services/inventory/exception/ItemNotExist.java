package com.management.cmdb.services.inventory.exception;

public class ItemNotExist extends RuntimeException {

    public ItemNotExist() {
        super("Item does not exist");
    }

    public ItemNotExist(String message) {
        super(message);
    }
}
