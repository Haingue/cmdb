package com.management.cmdb.services.inventory.mapper;

import com.management.cmdb.services.inventory.dto.LinkDto;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = LinkTypeMapper.class)
public interface LinkMapper {

    LinkMapper INSTANCE = Mappers.getMapper(LinkMapper.class);

    @Mapping(target = "sourceItemId", source = "linkEntity.sourceItem.uuid")
    @Mapping(target = "targetItemId", source = "linkEntity.targetItem.uuid")
    LinkDto toDto(LinkEntity linkEntity);

    @Mapping(target = "sourceItem.uuid", source = "linkDto.sourceItemId")
    @Mapping(target = "targetItem.uuid", source = "linkDto.targetItemId")
    LinkEntity toEntity(LinkDto linkDto);
}
