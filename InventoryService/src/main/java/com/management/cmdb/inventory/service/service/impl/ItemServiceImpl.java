package com.management.cmdb.inventory.service.service.impl;

import com.management.cmdb.inventory.service.entity.ItemEntity;
import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import com.management.cmdb.inventory.service.exception.ItemExist;
import com.management.cmdb.inventory.service.exception.ItemInvalid;
import com.management.cmdb.inventory.service.exception.ItemTypeNotExist;
import com.management.cmdb.inventory.service.model.UserDetail;
import com.management.cmdb.inventory.service.repository.ItemRepository;
import com.management.cmdb.inventory.service.repository.ItemTypeRepository;
import com.management.cmdb.inventory.service.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;
    private final ItemTypeRepository itemTypeRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ItemTypeRepository itemTypeRepository) {
        this.itemRepository = itemRepository;
        this.itemTypeRepository = itemTypeRepository;
    }

    @Override
    public ItemEntity createItem(ItemEntity newItem, UserDetail userDetail) {
        // TODO check user details

        if (newItem == null || newItem.getName() == null || newItem.getDescription() == null) {
            throw new ItemInvalid();
        }
        if (this.itemRepository.existsByNameAndType(newItem.getName(), newItem.getType())) {
            throw new ItemExist();
        }

        ItemTypeEntity itemTypeEntity = this.itemTypeRepository.findFirstByLabel(newItem.getType().getLabel())
                .orElseThrow(ItemTypeNotExist::new);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setUuid(UUID.randomUUID());
        itemEntity.setName(newItem.getName());
        itemEntity.setDescription(newItem.getDescription());
        itemEntity.setType(itemTypeEntity);
        itemEntity.setLinks(newItem.getLinks()); // TODO check and persist links
        return this.itemRepository.save(itemEntity);
    }

    @Override
    public ItemEntity updateItem(ItemEntity item, UserDetail userDetail) {
        ItemEntity existingItem = this.itemRepository.findById(item.getUuid())
                .orElseThrow(ItemTypeNotExist::new);
        existingItem.setName(item.getName());
        existingItem.setDescription(item.getDescription());
        existingItem.setLinks(item.getLinks());
        return this.itemRepository.save(existingItem);
    }

    @Override
    public void deleteItem(ItemEntity item, UserDetail userDetail) {
        ItemEntity existingItem = this.itemRepository.findById(item.getUuid())
                .orElseThrow(ItemTypeNotExist::new);
        this.itemRepository.delete(existingItem);
    }

    @Override
    public ItemEntity findItemById(UUID uuid, UserDetail userDetail) {
        return this.itemRepository.findById(uuid)
                .orElseThrow(ItemTypeNotExist::new);
    }

    @Override
    public Stream<ItemEntity> findItemByType(String itemTypeLabel, UserDetail userDetail) {
        return Stream.empty();
    }

    @Override
    public Stream<ItemEntity> findItemByNameLike(String itemName, UserDetail userDetail) {
        return Stream.empty();
    }
}
