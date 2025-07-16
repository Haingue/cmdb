package com.management.cmdb.inventory.service.exception;

public class ItemInvalid extends RuntimeException {

    public ItemInvalid() {
        super("Item is invalid");
    }

    public ItemInvalid(String message) {
        super(message);
    }
}
