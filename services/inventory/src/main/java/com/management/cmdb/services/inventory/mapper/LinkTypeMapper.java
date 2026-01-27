package com.management.cmdb.services.inventory.mapper;

import com.management.cmdb.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.services.inventory.entity.LinkTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LinkTypeMapper {

    LinkTypeMapper INSTANCE = Mappers.getMapper(LinkTypeMapper.class);

    LinkTypeDto toDto (LinkTypeEntity entity);
    LinkTypeEntity toEntity (LinkTypeDto dto);

}
