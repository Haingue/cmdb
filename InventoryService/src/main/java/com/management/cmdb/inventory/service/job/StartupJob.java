package com.management.cmdb.inventory.service.job;

import com.management.cmdb.inventory.service.model.itemTypes.DefaultItemType;
import com.management.cmdb.inventory.service.repository.ItemTypeRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StartupJob implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupJob.class);

    private final Environment env;

    private final ItemTypeRepository itemTypeRepository;

    public StartupJob(Environment env, ItemTypeRepository itemTypeRepository) {
        this.env = env;
        this.itemTypeRepository = itemTypeRepository;
    }

    @PostConstruct
    private void init() {
        for (DefaultItemType defaultItemType : DefaultItemType.values()) {
            if (this.itemTypeRepository.findFirstByLabel(defaultItemType.itemType.getLabel()).isEmpty()) {
                this.itemTypeRepository.save(defaultItemType.itemType);
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Starting up application");
        LOGGER.info("Profiles: {}", Arrays.toString(env.getActiveProfiles()));
        LOGGER.info("App version: {}", env.getProperty("spring.application.version"));
        LOGGER.info("Application started");
    }
}
