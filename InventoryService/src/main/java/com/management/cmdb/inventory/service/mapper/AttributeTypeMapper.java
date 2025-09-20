package com.management.cmdb.inventory.service.mapper;

import com.management.cmdb.inventory.service.dto.AttributeTypeDto;
import com.management.cmdb.inventory.service.entity.AttributeTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttributeTypeMapper {

    AttributeTypeMapper INSTANCE = Mappers.getMapper(AttributeTypeMapper.class);

    AttributeTypeEntity toEntity (AttributeTypeDto dto);
    AttributeTypeDto toDto (AttributeTypeEntity entity);

}
