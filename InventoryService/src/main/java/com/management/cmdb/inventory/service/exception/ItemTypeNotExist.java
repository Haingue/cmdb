package com.management.cmdb.inventory.service.exception;

public class ItemTypeNotExist extends RuntimeException {
    public ItemTypeNotExist() {
        super("Item type does not exist");
    }

    public ItemTypeNotExist(String message) {
        super(message);
    }
}
