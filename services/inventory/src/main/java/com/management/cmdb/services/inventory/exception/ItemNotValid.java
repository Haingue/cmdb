package com.management.cmdb.services.inventory.exception;

public class ItemNotValid extends RuntimeException {

    public ItemNotValid() {
        super("Item is invalid");
    }

    public ItemNotValid(String message) {
        super(message);
    }
}
