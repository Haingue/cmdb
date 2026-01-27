package com.management.cmdb.services.inventory.mapper;

import com.management.cmdb.services.inventory.dto.AttributeDto;
import com.management.cmdb.services.inventory.entity.AttributeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ItemTypeMapper.class)
public interface AttributeMapper {

    AttributeMapper INSTANCE = Mappers.getMapper(AttributeMapper.class);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "attributeTypeId", source = "attributeType.uuid")
    @Mapping(target = "label", source = "attributeType.label")
    @Mapping(target = "value", source = "valueStr")
    AttributeDto toDto (AttributeEntity entity);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "attributeType.uuid", source = "attributeTypeId")
    @Mapping(target = "attributeType.label", source = "label")
    @Mapping(target = "valueStr", source = "value")
    AttributeEntity toEntity (AttributeDto dto);

}
