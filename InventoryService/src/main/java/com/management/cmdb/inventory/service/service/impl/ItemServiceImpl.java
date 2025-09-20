package com.management.cmdb.inventory.service.service.impl;

import com.management.cmdb.inventory.service.dto.ItemDto;
import com.management.cmdb.inventory.service.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.inventory.service.entity.ItemEntity;
import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import com.management.cmdb.inventory.service.exception.ItemExist;
import com.management.cmdb.inventory.service.exception.ItemInvalid;
import com.management.cmdb.inventory.service.exception.ItemTypeNotExist;
import com.management.cmdb.inventory.service.mapper.ItemMapper;
import com.management.cmdb.inventory.service.model.UserDetail;
import com.management.cmdb.inventory.service.repository.ItemRepository;
import com.management.cmdb.inventory.service.repository.ItemTypeRepository;
import com.management.cmdb.inventory.service.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
    public ItemDto createItem(ItemDto newItemDto, UserDetail userDetail) {
        // TODO check user details

        if (newItemDto == null || newItemDto.name() == null || newItemDto.description() == null) {
            throw new ItemInvalid();
        }
        if (this.itemRepository.existsByNameAndTypeLabel(newItemDto.name(), newItemDto.type().label())) {
            throw new ItemExist();
        }

        ItemTypeEntity itemTypeEntity = this.itemTypeRepository.findFirstByLabel(newItemDto.type().label())
                .orElseThrow(ItemTypeNotExist::new);

        ItemEntity itemEntity = ItemMapper.toEntity(newItemDto);
        itemEntity.setUuid(UUID.randomUUID());
        itemEntity.setType(itemTypeEntity);
        itemEntity = this.itemRepository.save(itemEntity);
        return ItemMapper.toDto(itemEntity);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, UserDetail userDetail) {
        ItemEntity existingItem = this.itemRepository.findById(itemDto.uuid())
                .orElseThrow(ItemTypeNotExist::new);
        existingItem.setName(itemDto.name());
        existingItem.setDescription(itemDto.description());
        // TODO: existingItem.addToLinks(itemDto.toLinks());
        // TODO: existingItem.addFromLinks(itemDto.fromLinks());
        existingItem = this.itemRepository.save(existingItem);
        return ItemMapper.toDto(existingItem);
    }

    @Override
    public void deleteItem(UUID itemId, UserDetail userDetail) {
        ItemEntity existingItem = this.itemRepository.findById(itemId)
                .orElseThrow(ItemTypeNotExist::new);
        this.itemRepository.delete(existingItem);
    }

    @Override
    public ItemDto findItemById(UUID uuid, UserDetail userDetail) {
        return this.itemRepository.findById(uuid)
                .map(ItemMapper::toDto)
                .orElseThrow(ItemTypeNotExist::new);
    }

    @Override
    public PaginatedResponseDto<ItemDto> searchItemByNameOrType(String itemName, String itemTypeLabel, int page, int pageSize, UserDetail userDetail) {
        // TODO check user details
        Page<ItemEntity> result = this.itemRepository.searchAllByNameContainingIgnoreCaseOrTypeLabel(
                itemName,
                itemTypeLabel,
                PageRequest.of(page, pageSize));
        return ItemMapper.toPaginatedDto(result);
    }
}
