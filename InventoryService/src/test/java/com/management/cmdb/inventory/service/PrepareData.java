package com.management.cmdb.inventory.service;

import com.management.cmdb.inventory.service.exemple.ItemExample;
import com.management.cmdb.inventory.service.repository.ItemRepository;
import com.management.cmdb.inventory.service.repository.ItemTypeRepository;
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

    public PrepareData(Environment env, ItemRepository itemRepository, ItemTypeRepository itemTypeRepository) {
        this.env = env;
        this.itemRepository = itemRepository;
        this.itemTypeRepository = itemTypeRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("Starting up application");
        LOGGER.info("Profiles: {}", env.getActiveProfiles());
        LOGGER.info("App version: {}", env.getProperty("spring.application.version"));
        LOGGER.info("Application started");

        Arrays.stream(ItemExample.values())
                .map(ItemExample::toEntity)
                .forEach(itemRepository::save);
    }
}
