package com.management.cmdb.inventory.service.service.impl;

import com.management.cmdb.inventory.service.dto.AuthorDto;
import com.management.cmdb.inventory.service.dto.ItemDto;
import com.management.cmdb.inventory.service.dto.NotificationDto;
import com.management.cmdb.inventory.service.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.inventory.service.entity.ItemEntity;
import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import com.management.cmdb.inventory.service.exception.ItemExist;
import com.management.cmdb.inventory.service.exception.ItemNotValid;
import com.management.cmdb.inventory.service.exception.ItemTypeNotExist;
import com.management.cmdb.inventory.service.mapper.ItemMapper;
import com.management.cmdb.inventory.service.model.UserDetail;
import com.management.cmdb.inventory.service.repository.ItemRepository;
import com.management.cmdb.inventory.service.repository.ItemTypeRepository;
import com.management.cmdb.inventory.service.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ItemRepository itemRepository;
    private final ItemTypeRepository itemTypeRepository;

    public ItemServiceImpl(ApplicationEventPublisher applicationEventPublisher, ItemRepository itemRepository, ItemTypeRepository itemTypeRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.itemRepository = itemRepository;
        this.itemTypeRepository = itemTypeRepository;
    }

    @Override
    public ItemDto createItem(ItemDto newItemDto, UserDetail author) {
        // TODO check user details

        if (newItemDto == null || newItemDto.name() == null || newItemDto.description() == null) {
            throw new ItemNotValid();
        }
        if (this.itemRepository.existsByNameAndTypeLabel(newItemDto.name(), newItemDto.type().label())) {
            throw new ItemExist();
        }

        ItemTypeEntity itemTypeEntity = this.itemTypeRepository.findFirstByLabel(newItemDto.type().label())
                .orElseThrow(ItemTypeNotExist::new);

        ItemEntity itemEntity = ItemMapper.INSTANCE.toEntity(newItemDto);
        itemEntity.setUuid(UUID.randomUUID());
        itemEntity.setType(itemTypeEntity);

        itemEntity.getAttributes().forEach(attribute -> {
            attribute.setUuid(UUID.randomUUID());
            attribute.setCreatedBy(author.uuid());
        });

        itemEntity = this.itemRepository.save(itemEntity);
        ItemDto resultItem = ItemMapper.INSTANCE.toDto(itemEntity);
        // Send event
        applicationEventPublisher.publishEvent(
                new NotificationDto(
                        new AuthorDto(author.email()),
                        NotificationDto.NotificationType.NEW_ITEM,
                        "Create new item",
                        resultItem.uuid()
                )
        );

        return resultItem;
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
        return ItemMapper.INSTANCE.toDto(existingItem);
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
                .map(ItemMapper.INSTANCE::toDto)
                .orElseThrow(ItemTypeNotExist::new);
    }

    @Override
    public PaginatedResponseDto<ItemDto> searchItemByNameOrType(String itemName, String itemTypeLabel, int page, int pageSize, UserDetail userDetail) {
        // TODO check user details
        Page<ItemEntity> result = this.itemRepository.searchAllByNameContainingIgnoreCaseOrTypeLabel(
                itemName,
                itemTypeLabel,
                PageRequest.of(page, pageSize));
        return ItemMapper.INSTANCE.toPaginatedDto(result);
    }
}
