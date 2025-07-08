package com.management.cmdb.inventory.service.repository;

import com.management.cmdb.inventory.service.entity.ItemEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ItemRepositoryTest {

    @Resource
    private ItemRepository repository;

    @Test
    void shouldHaveRepository() {
        assertNotNull(repository);
    }

    @Test
    void shouldSaveItem() {
        ItemEntity item = new ItemEntity();
        item.setUuid(UUID.randomUUID());
        item.setName("test");
        item = repository.save(item);
        assertNotNull(item);
        assertNotNull(item.getUuid(), "Item uuid is null");
        assertEquals("test", item.getName(), "the name is wrong");
        assertNotNull(item.getCreatedDate(), "The creation date is null");
        assertNotNull(item.getCreatedBy(), "The creator is null");
    }

}