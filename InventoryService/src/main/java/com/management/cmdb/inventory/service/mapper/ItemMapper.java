package com.management.cmdb.inventory.service.mapper;

import com.management.cmdb.inventory.service.dto.ItemDto;
import com.management.cmdb.inventory.service.dto.ItemTypeDto;
import com.management.cmdb.inventory.service.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.inventory.service.entity.ItemEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toDto(ItemEntity entity) {
        ItemTypeDto itemTypeDto = null;
        if (entity.getType() != null) {
            itemTypeDto = ItemTypeMapper.INSTANCE.toDto(entity.getType());
        }
        return new ItemDto(
                entity.getUuid(),
                entity.getName(),
                entity.getDescription(),
                itemTypeDto,
                entity.getAttributes().stream().map(AttributeMapper.INSTANCE::toDto).collect(Collectors.toSet()),
                entity.getFromLinks().stream().map(LinkMapper::toDto).collect(Collectors.toSet()),
                entity.getToLinks().stream().map(LinkMapper::toDto).collect(Collectors.toSet()),
                entity.getCreatedDate(),
                entity.getCreatedBy(),
                entity.getLastModifiedBy(),
                entity.getLastModifiedDate()
                );
    }

    public static ItemEntity toEntity(ItemDto dto) {
        ItemEntity entity = new ItemEntity();
        entity.setUuid(dto.uuid());
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        if (dto.type() != null) {
            entity.setType(ItemTypeMapper.INSTANCE.toEntity(dto.type()));
        }

        entity.setCreatedBy(dto.createdBy());
        entity.setCreatedDate(dto.createdDate());
        entity.setLastModifiedBy(dto.lastModifiedBy());
        entity.setLastModifiedDate(dto.lastModifiedDate());

        // entity.addToLinks(dto.toLinks()); // TODO check and persist links
        // entity.addFromLinks(dto.fromLinks()); // TODO check and persist links
        return entity;
    }

    public static PaginatedResponseDto<ItemDto> toPaginatedDto(Page<ItemEntity> page) {
        List<ItemDto> content = page.getContent().stream()
                .map(ItemMapper::toDto)
                .toList();
        return new PaginatedResponseDto<ItemDto>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

}
