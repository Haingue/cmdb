package com.management.cmdb.inventory.service.mapper;

import com.management.cmdb.inventory.service.dto.AttributeDto;
import com.management.cmdb.inventory.service.entity.AttributeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AttributeMapper {

    AttributeMapper INSTANCE = Mappers.getMapper(AttributeMapper.class);

    AttributeEntity toEntity (AttributeDto dto);
    AttributeDto toDto (AttributeEntity entity);

}
