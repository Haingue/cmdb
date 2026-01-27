package com.management.cmdb.services.inventory.exception;

public class ItemTypeNotExist extends RuntimeException {
    public ItemTypeNotExist() {
        super("Item type does not exist");
    }

    public ItemTypeNotExist(String message) {
        super(message);
    }
}
