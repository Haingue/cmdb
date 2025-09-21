package com.management.cmdb.inventory.service.mapper;

import com.management.cmdb.inventory.service.dto.LinkTypeDto;
import com.management.cmdb.inventory.service.entity.LinkTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LinkTypeMapper {

    LinkTypeMapper INSTANCE = Mappers.getMapper(LinkTypeMapper.class);

    LinkTypeDto toDto (LinkTypeEntity entity);
    LinkTypeEntity toEntity (LinkTypeDto dto);

}
