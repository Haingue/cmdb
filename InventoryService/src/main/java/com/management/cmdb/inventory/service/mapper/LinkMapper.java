package com.management.cmdb.inventory.service.mapper;

import com.management.cmdb.inventory.service.dto.LinkDto;
import com.management.cmdb.inventory.service.entity.LinkEntity;

public class LinkMapper {

    public static LinkDto toDto(LinkEntity linkEntity) {
        return new LinkDto(
                LinkTypeMapper.toDto(linkEntity.getLinkType()),
                linkEntity.getFrom().getUuid(),
                ItemMapper.INSTANCE.toDto(linkEntity.getTo()),
                linkEntity.getDescription()
        );
    }

}
