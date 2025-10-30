package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import com.management.cmdb.services.inventory.exemple.ItemExample;
import com.management.cmdb.services.inventory.exemple.LinkTypeExample;
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
        assertEquals("test", item.getName(), "the label is wrong");
        assertNotNull(item.getCreatedDate(), "The creation date is null");
        assertNotNull(item.getCreatedBy(), "The creator is null");
    }

    @Test
    void shouldSaveItemLinks() {
        ItemEntity jettyItem = repository.save(ItemExample.JETTY01.toEntity());
        ItemEntity pgItem = repository.save(ItemExample.POSTGRESQL01.toEntity());

        LinkEntity jettyExchangeWithPg = new LinkEntity();
        jettyExchangeWithPg.setUuid(UUID.randomUUID());
        jettyExchangeWithPg.setLinkType(LinkTypeExample.COMMUNICATE_WITH.toEntity());
        jettyExchangeWithPg.setTargetItem(jettyItem);
        jettyExchangeWithPg.setSourceItem(pgItem);
        jettyItem.getOutgoingLinks().add(jettyExchangeWithPg);
        jettyItem = repository.save(jettyItem);

        assertNotNull(jettyItem);
        assertNotNull(jettyItem.getUuid(), "Item uuid is null");
        assertEquals(ItemExample.JETTY01.itemDto.name(), jettyItem.getName(), "the label is wrong");
        assertNotNull(jettyItem.getCreatedDate(), "The creation date is null");
        assertNotNull(jettyItem.getCreatedBy(), "The creator is null");
        assertNotNull(jettyItem.getOutgoingLinks(), "The link is null");
        assertFalse(jettyItem.getOutgoingLinks().isEmpty(), "The link are empty");
        LinkEntity expectedLink = jettyItem.getOutgoingLinks().stream().findFirst().get();
        assertNotNull(expectedLink, "The link is null (not saved)");
        assertEquals(expectedLink.getLinkType(), jettyExchangeWithPg.getLinkType(), "The link is wrong");
        assertEquals(expectedLink.getSourceItem(), pgItem, "The target item is wrong");
    }

}