package com.management.cmdb.backend.services.inventory.mapper.component;

import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.core.models.business.component.Component;

public interface ItemComponentMapper <T extends Component> {
    T mapToCoreModel(ItemDto itemDto);
    ItemDto mapToItemDto(T component);
}
