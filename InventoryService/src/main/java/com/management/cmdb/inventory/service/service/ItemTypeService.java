package com.management.cmdb.inventory.service.service;

import com.management.cmdb.inventory.service.dto.ItemTypeDto;
import com.management.cmdb.inventory.service.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.inventory.service.model.UserDetail;

import java.util.UUID;

public interface ItemTypeService {

    ItemTypeDto create(ItemTypeDto itemTypeDto, UserDetail author);

    ItemTypeDto findById(UUID id);
    PaginatedResponseDto<ItemTypeDto> search (String label, int page, int size);

}
