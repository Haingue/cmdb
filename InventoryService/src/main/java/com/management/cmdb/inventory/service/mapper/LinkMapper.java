package com.management.cmdb.inventory.service.mapper;

import com.management.cmdb.inventory.service.dto.LinkDto;
import com.management.cmdb.inventory.service.entity.LinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = LinkTypeMapper.class)
public interface LinkMapper {

    LinkMapper INSTANCE = Mappers.getMapper(LinkMapper.class);

    LinkDto toDto(LinkEntity linkEntity);
    LinkEntity toEntity(LinkDto linkDto);
}
