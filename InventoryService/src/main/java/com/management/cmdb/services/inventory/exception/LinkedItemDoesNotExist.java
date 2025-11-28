package com.management.cmdb.services.inventory.exception;

import com.management.cmdb.services.inventory.dto.ItemDto;
import com.management.cmdb.services.inventory.entity.ItemEntity;

import java.util.UUID;

public class LinkedItemDoesNotExist extends RuntimeException {

    public LinkedItemDoesNotExist() {
        super("Linked item does not exist");
    }

    public LinkedItemDoesNotExist(ItemEntity itemEntity) {
        super(String.format("Linked item %s does not exist", itemEntity.getUuid()));
    }
    public LinkedItemDoesNotExist(ItemDto itemDto) {
        super(String.format("Linked item %s does not exist", itemDto.uuid()));
    }
    public LinkedItemDoesNotExist(UUID itemId) {
        super(String.format("Linked item %s does not exist", itemId));
    }
}
