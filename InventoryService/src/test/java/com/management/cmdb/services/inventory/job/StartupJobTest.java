package com.management.cmdb.services.inventory.job;

import com.management.cmdb.services.inventory.entity.ItemTypeEntity;
import com.management.cmdb.services.inventory.exemple.ItemTypeExample;
import com.management.cmdb.services.inventory.repository.ItemTypeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class StartupJobTest {

    private final ItemTypeRepository itemTypeRepository;

    @Autowired
    public StartupJobTest(ItemTypeRepository itemTypeRepository) {
        this.itemTypeRepository = itemTypeRepository;
    }

    @Test
    void shouldHaveDefaultItemType() {
        for (ItemTypeExample itemTypeExample : ItemTypeExample.values()) {
            Optional<ItemTypeEntity> savedItemType = this.itemTypeRepository.findFirstByLabel(itemTypeExample.itemType.getLabel());
            Assertions.assertTrue(savedItemType.isPresent());
            Assertions.assertEquals(itemTypeExample.itemType.getLabel(), savedItemType.get().getLabel());
            Assertions.assertEquals(itemTypeExample.itemType.getDescription(), savedItemType.get().getDescription());
            Assertions.assertEquals(itemTypeExample.itemType.getAttributes().size(), savedItemType.get().getAttributes().size());
        }
    }

}