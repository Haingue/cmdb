package com.management.cmdb.inventory.service.mapper;

import com.management.cmdb.inventory.service.dto.ItemDto;
import com.management.cmdb.inventory.service.entity.ItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ItemTypeMapper.class, AttributeMapper.class, LinkMapper.class})
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDto toDto(ItemEntity entity);
    ItemEntity toEntity(ItemDto dto);

}
