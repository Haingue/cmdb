package com.management.cmdb.services.inventory.mapper;

import com.management.cmdb.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.services.inventory.entity.ItemTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AttributeTypeMapper.class)
public interface ItemTypeMapper {
    ItemTypeMapper INSTANCE = Mappers.getMapper(ItemTypeMapper.class);

    ItemTypeDto toDto(ItemTypeEntity entity);
    ItemTypeEntity toEntity(ItemTypeDto dto);

}
