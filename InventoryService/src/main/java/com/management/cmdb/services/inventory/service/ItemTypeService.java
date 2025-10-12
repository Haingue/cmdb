package com.management.cmdb.services.inventory.service;

import com.management.cmdb.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.inventory.model.UserDetail;

import java.util.UUID;

public interface ItemTypeService {

    ItemTypeDto create(ItemTypeDto itemTypeDto, UserDetail author);

    ItemTypeDto findById(UUID id);
    PaginatedResponseDto<ItemTypeDto> search (String label, int page, int size);

}
