package com.management.cmdb.inventory.service.exception;

public class ItemTypeNotValid extends RuntimeException {

    public ItemTypeNotValid() {
        super("Item is invalid");
    }

    public ItemTypeNotValid(String message) {
        super(message);
    }
}
