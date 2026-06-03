package com.management.cmdb.services.inventory.service;

import com.management.cmdb.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.inventory.model.UserDetail;

import java.util.Optional;
import java.util.UUID;

public interface ItemTypeService {

    ItemTypeDto create(ItemTypeDto itemTypeDto, UserDetail author);

    Optional<ItemTypeDto> findById(UUID id);
    Optional<ItemTypeDto> findByLabel(String label);
    PaginatedResponseDto<ItemTypeDto> search (String label, int page, int size);

}
