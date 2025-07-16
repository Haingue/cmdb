package com.management.cmdb.inventory.service.exemple;

import com.management.cmdb.inventory.service.entity.ItemEntity;
import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import com.management.cmdb.inventory.service.entity.LinkEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public enum ItemExample {

    JETTY01("JETTY 01", "First Jetty server", ItemTypeExample.SERVER);

    private final String name;
    private final String description;
    private final ItemTypeEntity type;
    private final List<LinkEntity> links;
    public final UUID uuid;
    private final LocalDateTime createdDate;
    private final UUID createdBy;
    private final UUID lastModifiedBy;
    private final LocalDateTime lastModifiedDate;

    ItemExample(String name, String description, ItemTypeExample type) {
        this.name = name;
        this.description = description;
        this.type = type.toEntity();
        this.links = new ArrayList<>();

        this.uuid = new UUID(20, 0);
        this.createdDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        this.createdBy = new UUID(0, 0);
        this.lastModifiedDate = LocalDateTime.of(2025, 2, 1, 0, 0);
        this.lastModifiedBy = new UUID(0, 0);
    }

    public ItemEntity toEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName(name);
        itemEntity.setDescription(description);
        itemEntity.setType(type);
        itemEntity.setLinks(links);
        itemEntity.setUuid(uuid);
        itemEntity.setCreatedDate(createdDate);
        itemEntity.setCreatedBy(createdBy);
        itemEntity.setLastModifiedDate(lastModifiedDate);
        itemEntity.setLastModifiedBy(lastModifiedBy);
        return itemEntity;
    }
}
