package com.management.cmdb.inventory.service.exemple;

import com.management.cmdb.inventory.service.entity.ItemTypeEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public enum ItemTypeExample {

    SERVER("Server", "Server"),
    DATABASE("Database", "Database");

    public final String label;
    public final String description;

    public final UUID uuid;
    private final LocalDateTime createdDate;
    private final UUID createdBy;
    private final UUID lastModifiedBy;
    private final LocalDateTime lastModifiedDate;

    ItemTypeExample(String label, String description) {
        this.label = label;
        this.description = description;
        this.uuid = new UUID(10, 0);
        this.createdDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        this.createdBy = new UUID(0, 0);
        this.lastModifiedDate = LocalDateTime.of(2025, 2, 1, 0, 0);
        this.lastModifiedBy = new UUID(0, 0);
    }

    public ItemTypeEntity toEntity () {
        ItemTypeEntity itemTypeEntity = new ItemTypeEntity();
        itemTypeEntity.setLabel(label);
        itemTypeEntity.setDescription(description);
        itemTypeEntity.setUuid(uuid);
        itemTypeEntity.setCreatedDate(createdDate);
        itemTypeEntity.setCreatedBy(createdBy);
        itemTypeEntity.setLastModifiedDate(lastModifiedDate);
        itemTypeEntity.setLastModifiedBy(lastModifiedBy);
        return itemTypeEntity;
    }
}
