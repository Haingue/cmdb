package com.management.cmdb.services.inventory.service;

import com.management.cmdb.services.inventory.dto.ItemDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.inventory.model.UserDetail;

import java.util.UUID;

public interface ItemService {

    ItemDto createItem (ItemDto newItem, UserDetail userDetail);
    ItemDto updateItem (ItemDto item, UserDetail userDetail);
    void deleteItem (UUID itemId, UserDetail userDetail);

    ItemDto findItemById (UUID uuid, UserDetail userDetail);
    PaginatedResponseDto<ItemDto> searchItemByNameOrType(String itemName, String itemTypeLabel, int page, int pageSize, UserDetail userDetail);

}
