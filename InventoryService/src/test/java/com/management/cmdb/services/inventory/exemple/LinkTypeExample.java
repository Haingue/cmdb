package com.management.cmdb.services.inventory.exemple;

import com.management.cmdb.services.inventory.dto.LinkTypeDto;
import com.management.cmdb.services.inventory.entity.LinkTypeEntity;
import com.management.cmdb.services.inventory.mapper.LinkTypeMapper;

import java.util.UUID;

public enum LinkTypeExample {

    COMMUNICATE_WITH("c3e08421-0e97-40a0-ad45-bad1977b0893"),
    DEPENDENT_WITH("16a419c6-b6af-4053-a756-489192b7021a");

    private final LinkTypeEntity linkType;

    LinkTypeExample(String uuid) {
        this.linkType = new LinkTypeEntity();
        this.linkType.setUuid(UUID.fromString(uuid));
        this.linkType.setLabel(name());
    }

    public LinkTypeEntity toEntity() {
        return this.linkType;
    }

    public LinkTypeDto getLinkTypeDto() {
        return LinkTypeMapper.INSTANCE.toDto(this.linkType);
    }
}
