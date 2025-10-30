package com.management.cmdb.services.inventory.exemple;

import com.management.cmdb.services.inventory.dto.AttributeDto;
import com.management.cmdb.services.inventory.dto.ItemDto;
import com.management.cmdb.services.inventory.dto.LinkDto;
import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.mapper.ItemMapper;
import com.management.cmdb.services.inventory.mapper.ItemTypeMapper;
import com.management.cmdb.services.inventory.model.UserDetail;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public enum ItemExample {

    JETTY01("787860cd-9e3c-4198-8455-c7e7457c1d21", "JETTY 01", "First Jetty server", ItemTypeExample.HOST, Map.of("hostname", "MYSERVERJETTY01"), Set.of(), Set.of()),
    POSTGRESQL01("74bf882b-ecaf-4575-a142-2a6f46cd79b9", "PostgreSQL 01", "First PostGreSQL server", ItemTypeExample.HOST, Map.of("hostname", "MYSERVERDB01"), Set.of(), Set.of());

    public final ItemDto itemDto;

    ItemExample(String uuid, String name, String description, ItemTypeExample type, Map<String, String> attributes, Set<LinkDto> outgoingLinks, Set<LinkDto> incomingLinks) {
        this.itemDto = new ItemDto(
                UUID.fromString(uuid),
                name,
                description,
                ItemTypeMapper.INSTANCE.toDto(type.itemType),
                attributes.entrySet().stream().map(entry ->
                        new AttributeDto(
                                UUID.randomUUID(),
                                entry.getKey(),
                                type.itemType.getAttributes().stream()
                                        .filter(attributeType -> attributeType.getLabel().equals(entry.getKey()))
                                        .findFirst()
                                        .get()
                                        .getUuid(),
                                entry.getValue(),
                                LocalDateTime.of(2025, 1, 1, 0, 0),
                                UserDetail.SYSTEM.uuid(),
                                UserDetail.SYSTEM.uuid(),
                                LocalDateTime.of(2025, 1, 1, 0, 0)))
                        .collect(Collectors.toSet()),
                outgoingLinks,
                incomingLinks,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                UserDetail.SYSTEM.uuid(),
                UserDetail.SYSTEM.uuid(),
                LocalDateTime.of(2025, 1, 1, 0, 0)
        );
    }

    public ItemDto toDto () {
        return itemDto;
    }

    public ItemEntity toEntity() {
        return ItemMapper.INSTANCE.toEntity(itemDto);
    }
}
