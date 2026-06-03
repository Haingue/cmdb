package com.management.cmdb.services.inventory.mapper;

import com.management.cmdb.services.inventory.dto.AttributeTypeDto;
import com.management.cmdb.services.inventory.entity.AttributeTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttributeTypeMapper {

    AttributeTypeMapper INSTANCE = Mappers.getMapper(AttributeTypeMapper.class);

    AttributeTypeEntity toEntity (AttributeTypeDto dto);
    AttributeTypeDto toDto (AttributeTypeEntity entity);

}
