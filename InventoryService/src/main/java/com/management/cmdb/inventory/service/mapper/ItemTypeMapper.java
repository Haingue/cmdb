package com.management.cmdb.inventory.service.mapper;

import com.management.cmdb.inventory.service.dto.ItemTypeDto;
import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AttributeTypeMapper.class)
public interface ItemTypeMapper {
    ItemTypeMapper INSTANCE = Mappers.getMapper(ItemTypeMapper.class);

    ItemTypeDto toDto(ItemTypeEntity entity);
    ItemTypeEntity toEntity(ItemTypeDto dto);

}
