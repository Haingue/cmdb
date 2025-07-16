package com.management.cmdb.inventory.service.service;

import com.management.cmdb.inventory.service.entity.ItemEntity;
import com.management.cmdb.inventory.service.model.UserDetail;

import java.util.UUID;
import java.util.stream.Stream;

public interface ItemService {

    ItemEntity createItem (ItemEntity newItem, UserDetail userDetail);
    ItemEntity updateItem (ItemEntity item, UserDetail userDetail);
    void deleteItem (ItemEntity item, UserDetail userDetail);

    ItemEntity findItemById (UUID uuid, UserDetail userDetail);
    Stream<ItemEntity> findItemByType (String itemTypeLabel, UserDetail userDetail);
    Stream<ItemEntity> findItemByNameLike (String itemName, UserDetail userDetail);

}
