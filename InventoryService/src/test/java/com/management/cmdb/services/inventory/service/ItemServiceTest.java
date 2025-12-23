package com.management.cmdb.services.inventory.service;

import com.management.cmdb.services.inventory.dto.ItemDto;
import com.management.cmdb.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.entity.ItemTypeEntity;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import com.management.cmdb.services.inventory.exemple.ItemExample;
import com.management.cmdb.services.inventory.exemple.ItemTypeExample;
import com.management.cmdb.services.inventory.exemple.LinkTypeExample;
import com.management.cmdb.services.inventory.mapper.ItemMapper;
import com.management.cmdb.services.inventory.model.UserDetail;
import com.management.cmdb.services.inventory.repository.ItemRepository;
import com.management.cmdb.services.inventory.repository.ItemTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ItemServiceTest {

    private final ItemTypeRepository itemTypeRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    private final ItemTypeEntity itemTypeExample;
    private final ItemEntity itemExample;
    private final UserDetail userDetail;

    @Autowired
    public ItemServiceTest(ItemTypeRepository itemTypeRepository, ItemRepository itemRepository, ItemService itemService) {
        this.itemTypeRepository = itemTypeRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;

        this.itemTypeExample = itemTypeRepository.save(ItemTypeExample.HOST.itemType);
        this.itemExample = itemRepository.save(ItemExample.JETTY01.toEntity());
        this.userDetail = new UserDetail(new UUID(0, 0), "test","test@test.com");
    }

    @Test
    void createItem() {
        ItemEntity newItem = ItemExample.JETTY01.toEntity();
        newItem.setName("New Jetty");
        ItemDto savedItem = this.itemService.createItem(ItemMapper.INSTANCE.toDto(newItem), this.userDetail);
        Assertions.assertNotNull(savedItem);
        Assertions.assertNotNull(savedItem.uuid());
        Assertions.assertNotNull(savedItem.createdDate());
        Assertions.assertNotNull(savedItem.createdBy());
        Assertions.assertNotEquals(newItem.getUuid(), savedItem.uuid());
        Assertions.assertEquals(newItem.getName(), savedItem.name());
    }

    @Test
    void updateItem() {
        this.itemExample.setName("UpdatedItem");
        ItemDto savedItem = this.itemService.updateItem(ItemMapper.INSTANCE.toDto(this.itemExample), this.userDetail);
        Assertions.assertNotNull(savedItem);
        Assertions.assertNotNull(savedItem.uuid());
        Assertions.assertNotNull(savedItem.lastModifiedDate());
        Assertions.assertNotNull(savedItem.lastModifiedBy());
        Assertions.assertEquals(this.itemExample.getName(), savedItem.name());
    }

    @Test
    void findItemById() {
        Assertions.assertDoesNotThrow(() -> {
            ItemDto itemFound = this.itemService.findItemById(this.itemExample.getUuid(), this.userDetail);
            Assertions.assertNotNull(itemFound);
            Assertions.assertEquals(this.itemExample.getUuid(), itemFound.uuid());
            Assertions.assertEquals(this.itemExample.getName(), itemFound.name());
        });
    }

    @Test
    void findComplexItemById() {
        ItemEntity pgItem = ItemExample.POSTGRESQL01.toEntity();
        LinkEntity jettyExchangeWithPg = new LinkEntity();
        jettyExchangeWithPg.setUuid(UUID.randomUUID());
        jettyExchangeWithPg.setDescription("1st  link description");
        jettyExchangeWithPg.setLinkType(LinkTypeExample.COMMUNICATE_WITH.toEntity());
        jettyExchangeWithPg.setSourceItem(pgItem);
        jettyExchangeWithPg.setTargetItem(this.itemExample);
        this.itemExample.getIncomingLinks().add(jettyExchangeWithPg);
        this.itemRepository.save(this.itemExample);

        Assertions.assertDoesNotThrow(() -> {
            ItemDto itemFound = this.itemService.findItemById(this.itemExample.getUuid(), this.userDetail);
            Assertions.assertNotNull(itemFound);
            Assertions.assertEquals(this.itemExample.getUuid(), itemFound.uuid());
            Assertions.assertEquals(this.itemExample.getName(), itemFound.name());
        });
    }

    @Test
    void shouldFindItemOnlyByNameResearch() {
        String label = this.itemExample.getName().toLowerCase();
        PaginatedResponseDto<ItemDto> itemList = this.itemService.searchItemByNameOrType(label, this.itemExample.getType().getLabel(), 0, 3, this.userDetail);
        Assertions.assertFalse(itemList.isEmpty());
        Optional<ItemDto> itemFound = itemList.content().stream().findFirst();
        Assertions.assertEquals(this.itemExample.getUuid(), itemFound.get().uuid());
        Assertions.assertEquals(this.itemExample.getName(), itemFound.get().name());
        Assertions.assertEquals(this.itemTypeExample.getLabel(), itemFound.get().type().label());
    }

    @Test
    void shouldFindItemOnlyByTypeResearch() {
        PaginatedResponseDto<ItemDto> itemList = this.itemService.searchItemByNameOrType(null, this.itemExample.getType().getLabel(), 0, 3, this.userDetail);
        Assertions.assertFalse(itemList.isEmpty());
        Optional<ItemDto> itemFound = itemList.content().stream().findFirst();
        Assertions.assertEquals(this.itemExample.getUuid(), itemFound.get().uuid());
        Assertions.assertEquals(this.itemExample.getName(), itemFound.get().name());
        Assertions.assertEquals(this.itemTypeExample.getLabel(), itemFound.get().type().label());
    }

    @Test
    void shouldFindItemByNameAndTypeResearch() {
        String label = this.itemExample.getName().toLowerCase();
        PaginatedResponseDto<ItemDto> itemList = this.itemService.searchItemByNameOrType(label, this.itemExample.getType().getLabel(), 0, 3, this.userDetail);
        Assertions.assertFalse(itemList.isEmpty());
        Optional<ItemDto> itemFound = itemList.content().stream().findFirst();
        Assertions.assertEquals(this.itemExample.getUuid(), itemFound.get().uuid());
        Assertions.assertEquals(this.itemExample.getName(), itemFound.get().name());
        Assertions.assertEquals(this.itemTypeExample.getLabel(), itemFound.get().type().label());
    }

    @Test
    @Transactional
    void deleteItem() {
        this.itemService.deleteItem(this.itemExample.getUuid(), userDetail);

        Assertions.assertTrue(this.itemRepository.findById(this.itemExample.getUuid()).isEmpty());
    }

    @Test
    @Transactional
    void shouldNotDuplicateItemType () {
        Optional<ItemTypeEntity> searchItemType = this.itemTypeRepository.findFirstByLabel(this.itemTypeExample.getLabel());
        Assertions.assertTrue(searchItemType.isPresent());
        ItemTypeEntity itemTypeEntity = searchItemType.get();
        itemTypeEntity = this.itemTypeRepository.save(itemTypeEntity);
        itemTypeEntity = this.itemTypeRepository.save(itemTypeEntity);
        itemTypeEntity = this.itemTypeRepository.save(itemTypeEntity);
        itemTypeEntity = this.itemTypeRepository.save(itemTypeEntity);
        Assertions.assertEquals(itemTypeEntity.getLabel(), this.itemTypeExample.getLabel());


        ItemEntity newItem = ItemExample.JETTY01.toEntity();
        newItem.setName("New Jetty 01");
        this.itemService.createItem(ItemMapper.INSTANCE.toDto(newItem), this.userDetail);
        newItem = ItemExample.JETTY01.toEntity();
        newItem.setName("New Jetty 02");
        this.itemService.createItem(ItemMapper.INSTANCE.toDto(newItem), this.userDetail);
        newItem = ItemExample.JETTY01.toEntity();
        newItem.setName("New Jetty 03");
        this.itemService.createItem(ItemMapper.INSTANCE.toDto(newItem), this.userDetail);

        Page<ItemTypeEntity> itemTypeList = this.itemTypeRepository.searchAllByLabelContainingIgnoreCase(this.itemTypeExample.getLabel(), PageRequest.of(0, 10));
        Assertions.assertEquals(2L, itemTypeList.getTotalElements());
    }
}