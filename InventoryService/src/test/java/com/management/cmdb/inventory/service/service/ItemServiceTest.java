package com.management.cmdb.inventory.service.service;

import com.management.cmdb.inventory.service.dto.ItemDto;
import com.management.cmdb.inventory.service.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.inventory.service.entity.ItemEntity;
import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import com.management.cmdb.inventory.service.exemple.ItemExample;
import com.management.cmdb.inventory.service.mapper.ItemMapper;
import com.management.cmdb.inventory.service.model.UserDetail;
import com.management.cmdb.inventory.service.model.itemTypes.DefaultItemType;
import com.management.cmdb.inventory.service.repository.ItemRepository;
import com.management.cmdb.inventory.service.repository.ItemTypeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ItemServiceTest {

    private final ItemRepository itemRepository;
    private final ItemService itemService;

    private final ItemTypeEntity itemTypeExample;
    private final ItemEntity itemExample;
    private final UserDetail userDetail;

    @Autowired
    public ItemServiceTest(ItemTypeRepository itemTypeRepository, ItemRepository itemRepository, ItemService itemService) {
        this.itemRepository = itemRepository;
        this.itemService = itemService;

        this.itemTypeExample = DefaultItemType.HOST.itemType;
        this.itemExample = itemRepository.save(ItemExample.JETTY01.toEntity());
        this.userDetail = new UserDetail(new UUID(0, 0), "test","test@test.com");
    }

    @Test
    void createItem() {
        ItemEntity newItem = ItemExample.JETTY01.toEntity();
        newItem.setName("New Jetty");
        ItemDto savedItem = this.itemService.createItem(ItemMapper.toDto(newItem), this.userDetail);
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
        ItemDto savedItem = this.itemService.updateItem(ItemMapper.toDto(this.itemExample), this.userDetail);
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
    void deleteItem() {
        this.itemService.deleteItem(this.itemExample.getUuid(), userDetail);

        Assertions.assertTrue(this.itemRepository.findById(this.itemExample.getUuid()).isEmpty());
    }
}