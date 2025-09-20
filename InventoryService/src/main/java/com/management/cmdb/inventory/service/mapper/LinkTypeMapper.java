package com.management.cmdb.inventory.service.mapper;

import com.management.cmdb.inventory.service.dto.LinkTypeDto;
import com.management.cmdb.inventory.service.entity.LinkTypeEntity;

public class LinkTypeMapper {

    public static LinkTypeDto toDto (LinkTypeEntity entity) {
        return new LinkTypeDto(entity.getLabel());
    }

}
