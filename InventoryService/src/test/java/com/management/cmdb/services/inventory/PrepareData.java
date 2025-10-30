package com.management.cmdb.services.inventory;

import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.exemple.ItemExample;
import com.management.cmdb.services.inventory.exemple.ItemTypeExample;
import com.management.cmdb.services.inventory.exemple.LinkTypeExample;
import com.management.cmdb.services.inventory.repository.ItemRepository;
import com.management.cmdb.services.inventory.repository.ItemTypeRepository;
import com.management.cmdb.services.inventory.repository.LinkTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PrepareData implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrepareData.class);

    private final Environment env;

    private final ItemRepository itemRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final LinkTypeRepository linkTypeRepository;

    public PrepareData(Environment env, ItemRepository itemRepository, ItemTypeRepository itemTypeRepository, LinkTypeRepository linkTypeRepository) {
        this.env = env;
        this.itemRepository = itemRepository;
        this.itemTypeRepository = itemTypeRepository;
        this.linkTypeRepository = linkTypeRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("Starting up application");
        LOGGER.info("Profiles: {}", Arrays.toString(env.getActiveProfiles()));
        LOGGER.info("App version: {}", env.getProperty("spring.application.version"));
        LOGGER.info("Application started");

        for (LinkTypeExample linkTypeExample : LinkTypeExample.values()) {
            linkTypeRepository.save(linkTypeExample.toEntity());
        }
        for (ItemTypeExample itemTypeExample : ItemTypeExample.values()) {
            itemTypeRepository.save(itemTypeExample.itemType);
        }
        for (ItemExample itemExample : ItemExample.values()) {
            ItemEntity entity = itemExample.toEntity();
            itemRepository.save(entity);
        }
    }
}
