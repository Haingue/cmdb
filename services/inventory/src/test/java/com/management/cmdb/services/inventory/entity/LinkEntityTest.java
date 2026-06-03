package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.exemple.ItemExample;
import com.management.cmdb.services.inventory.exemple.LinkTypeExample;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LinkEntityTest {

    @Test
    void shouldAddSeveralLinkInSet() {
        ItemEntity existingItem = ItemExample.JETTY01.toEntity();

        ItemEntity pgItem = ItemExample.POSTGRESQL01.toEntity();
        LinkEntity jettyExchangeWithPg = new LinkEntity();
        jettyExchangeWithPg.setUuid(UUID.randomUUID());
        jettyExchangeWithPg.setLinkType(LinkTypeExample.COMMUNICATE_WITH.toEntity());
        jettyExchangeWithPg.setSourceItem(existingItem);
        jettyExchangeWithPg.setTargetItem(pgItem);

        ItemEntity pgItem2 = ItemExample.POSTGRESQL02.toEntity();
        LinkEntity jettyExchangeWithPg2 = new LinkEntity();
        jettyExchangeWithPg2.setUuid(UUID.randomUUID());
        jettyExchangeWithPg2.setLinkType(LinkTypeExample.COMMUNICATE_WITH.toEntity());
        jettyExchangeWithPg2.setSourceItem(existingItem);
        jettyExchangeWithPg2.setTargetItem(pgItem2);

        assertEquals(2, Set.of(jettyExchangeWithPg, jettyExchangeWithPg2).size());
    }

}