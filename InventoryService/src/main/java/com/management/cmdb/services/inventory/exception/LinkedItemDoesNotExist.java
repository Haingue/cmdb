package com.management.cmdb.services.inventory.exception;

import com.management.cmdb.services.inventory.entity.LinkEntity;

public class LinkedItemDoesNotExist extends RuntimeException {

    public LinkedItemDoesNotExist() {
        super("Linked item does not exist");
    }

    public LinkedItemDoesNotExist(LinkEntity link) {
        super(String.format("Linked item %s does not exist", link));
    }
}
