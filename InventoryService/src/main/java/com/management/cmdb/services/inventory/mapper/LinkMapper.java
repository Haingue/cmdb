package com.management.cmdb.services.inventory.mapper;

import com.management.cmdb.services.inventory.dto.LinkDto;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = LinkTypeMapper.class)
public interface LinkMapper {

    LinkMapper INSTANCE = Mappers.getMapper(LinkMapper.class);

    LinkDto toDto(LinkEntity linkEntity);
    LinkEntity toEntity(LinkDto linkDto);
}
