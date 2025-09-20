package com.management.cmdb.inventory.service.exemple;

import com.management.cmdb.inventory.service.dto.AttributeDto;
import com.management.cmdb.inventory.service.dto.ItemDto;
import com.management.cmdb.inventory.service.dto.LinkDto;
import com.management.cmdb.inventory.service.entity.*;
import com.management.cmdb.inventory.service.mapper.AttributeMapper;
import com.management.cmdb.inventory.service.mapper.ItemMapper;
import com.management.cmdb.inventory.service.mapper.ItemTypeMapper;
import com.management.cmdb.inventory.service.model.UserDetail;
import com.management.cmdb.inventory.service.model.itemTypes.DefaultItemType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public enum ItemExample {

    JETTY01("JETTY 01", "First Jetty server", DefaultItemType.HOST, Map.of("hostname", "MYSERVERJETTY01"), Set.of(), Set.of());

    public final ItemDto itemDto;

    ItemExample(String name, String description, DefaultItemType type, Map<String, String> attributes, Set<LinkDto> fromLinks, Set<LinkDto> toLinks) {
        this.itemDto = new ItemDto(
                UUID.randomUUID(),
                name,
                description,
                ItemTypeMapper.INSTANCE.toDto(type.itemType),
                attributes.entrySet().stream().map(entry ->
                        new AttributeDto(
                                UUID.randomUUID(),
                                entry.getKey(),
                                entry.getValue(),
                                LocalDateTime.of(2025, 1, 1, 0, 0),
                                UserDetail.SYSTEM.uuid(),
                                UserDetail.SYSTEM.uuid(),
                                LocalDateTime.of(2025, 1, 1, 0, 0)))
                        .collect(Collectors.toSet()),
                fromLinks,
                toLinks,
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
