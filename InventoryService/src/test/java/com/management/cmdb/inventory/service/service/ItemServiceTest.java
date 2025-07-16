package com.management.cmdb.inventory.service.service;

import com.management.cmdb.inventory.service.entity.ItemEntity;
import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import com.management.cmdb.inventory.service.exemple.ItemExample;
import com.management.cmdb.inventory.service.exemple.ItemTypeExample;
import com.management.cmdb.inventory.service.model.UserDetail;
import com.management.cmdb.inventory.service.repository.ItemRepository;
import com.management.cmdb.inventory.service.repository.ItemTypeRepository;
import jakarta.persistence.OrderBy;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

        this.itemTypeExample = itemTypeRepository.save(ItemTypeExample.SERVER.toEntity());
        this.itemExample = itemRepository.save(ItemExample.JETTY01.toEntity());
        this.userDetail = new UserDetail(new UUID(0, 0), "test","test@test.com");
    }

    @Test
    @Order(1)
    void createItem() {
        ItemEntity newItem = ItemExample.JETTY01.toEntity();
        newItem.setName("New Jetty");
        ItemEntity savedItem = this.itemService.createItem(newItem, this.userDetail);
        Assertions.assertNotNull(savedItem);
        Assertions.assertNotNull(savedItem.getUuid());
        Assertions.assertNotNull(savedItem.getCreatedDate());
        Assertions.assertNotNull(savedItem.getCreatedBy());
        Assertions.assertNotEquals(newItem.getUuid(), savedItem.getUuid());
        Assertions.assertEquals(newItem.getName(), savedItem.getName());
    }

    @Test
    @Order(2)
    void updateItem() {
        this.itemExample.setName("UpdatedItem");
        ItemEntity savedItem = this.itemService.updateItem(this.itemExample, this.userDetail);
        Assertions.assertNotNull(savedItem);
        Assertions.assertNotNull(savedItem.getUuid());
        Assertions.assertNotNull(savedItem.getLastModifiedDate());
        Assertions.assertNotNull(savedItem.getLastModifiedBy());
        Assertions.assertEquals(this.itemExample.getName(), savedItem.getName());
    }

    @Test
    @Order(3)
    void findItemById() {
        Assertions.assertDoesNotThrow(() -> {
            ItemEntity itemFound = this.itemService.findItemById(this.itemExample.getUuid(), this.userDetail);
            Assertions.assertNotNull(itemFound);
            Assertions.assertEquals(this.itemExample.getUuid(), itemFound.getUuid());
            Assertions.assertEquals(this.itemExample.getName(), itemFound.getName());
        });
    }

    @Test
    @Order(4)
    void findItemByType() {
        List<ItemEntity> itemList = this.itemService.findItemByType(this.itemExample.getType().getLabel(), this.userDetail).toList();
        Assertions.assertFalse(itemList.isEmpty());
        Optional<ItemEntity> itemFound = itemList.stream().findFirst();
        Assertions.assertEquals(this.itemExample.getUuid(), itemFound.get().getUuid());
        Assertions.assertEquals(this.itemExample.getName(), itemFound.get().getName());
        Assertions.assertEquals(this.itemTypeExample, itemFound.get().getType());
    }

    @Test
    @Order(5)
    void findItemByNameLike() {
        String labelLike = String.format(".*%s.*", this.itemExample.getName().toLowerCase());
        List<ItemEntity> itemList = this.itemService.findItemByType(labelLike, this.userDetail).toList();
        Assertions.assertFalse(itemList.isEmpty());
        Optional<ItemEntity> itemFound = itemList.stream().findFirst();
        Assertions.assertEquals(this.itemExample.getUuid(), itemFound.get().getUuid());
        Assertions.assertEquals(this.itemExample.getName(), itemFound.get().getName());
        Assertions.assertEquals(this.itemTypeExample, itemFound.get().getType());
    }

    @Test
    @Order(6)
    void deleteItem() {
        this.itemService.deleteItem(this.itemExample, userDetail);

        Assertions.assertTrue(this.itemRepository.findById(this.itemExample.getUuid()).isEmpty());
    }
}