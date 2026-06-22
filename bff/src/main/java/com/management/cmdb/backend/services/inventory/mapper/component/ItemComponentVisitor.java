package com.management.cmdb.backend.services.inventory.mapper.component;

import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.core.models.business.component.*;
import com.management.cmdb.core.models.exceptions.NotImplemented;
import com.management.cmdb.core.models.technical.ComponentVisitor;

@org.springframework.stereotype.Component
public class ItemComponentVisitor implements ComponentVisitor<ItemDto> {
    @Override
    public ItemDto accept(Component component) {
        return ItemGenericComponentMapper.INSTANCE.mapToItemDto((GenericComponent) component);
    }
    @Override
    public ItemDto accept(GenericComponent component) {
        return ItemGenericComponentMapper.INSTANCE.mapToItemDto(component);
    }

    @Override
    public ItemDto accept(Host host) {
        return ItemHostMapper.INSTANCE.mapToItemDto(host);
    }

    @Override
    public ItemDto accept(Hardware hardware) {
        return ItemHardwareMapper.INSTANCE.mapToItemDto(hardware);
    }

    @Override
    public ItemDto accept(Software software) {
        return ItemSoftwareMapper.INSTANCE.mapToItemDto(software);
    }

    @Override
    public ItemDto accept(VirtualMachine virtualMachine) {
        throw new NotImplemented();
    }
}
