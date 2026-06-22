package com.management.cmdb.backend.services.inventory.mapper.component;

import com.management.cmdb.core.models.business.constant.ComponentType;

public class ComponentMapperFactory {

    public static ItemComponentMapper<?> getMapperFor(ComponentType type) {
        return switch (type) {
            case HOST -> ItemHostMapper.INSTANCE;
            case SOFTWARE -> ItemSoftwareMapper.INSTANCE;
            case HARDWARE -> ItemHardwareMapper.INSTANCE;
            // TODO: add VirtualHost
            default -> ItemGenericComponentMapper.INSTANCE;
        };
    }

}
