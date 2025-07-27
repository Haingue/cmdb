package com.management.cmdb.inventory.service.job;

import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import com.management.cmdb.inventory.service.model.itemTypes.DefaultItemType;
import com.management.cmdb.inventory.service.repository.ItemTypeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
        for (DefaultItemType defaultItemType : DefaultItemType.values()) {
            Optional<ItemTypeEntity> savedItemType = this.itemTypeRepository.findFirstByLabel(defaultItemType.itemType.getLabel());
            Assertions.assertTrue(savedItemType.isPresent());
            Assertions.assertEquals(defaultItemType.itemType.getLabel(), savedItemType.get().getLabel());
            Assertions.assertEquals(defaultItemType.itemType.getDescription(), savedItemType.get().getDescription());
            Assertions.assertEquals(defaultItemType.itemType.getAttributes().size(), savedItemType.get().getAttributes().size());
        }
    }

}