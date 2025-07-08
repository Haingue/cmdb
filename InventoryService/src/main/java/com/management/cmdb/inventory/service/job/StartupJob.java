package com.management.cmdb.inventory.service.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupJob implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupJob.class);

    private Environment env;

    public StartupJob(Environment env) {
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("Starting up application");
        LOGGER.info("Profiles: {}", env.getActiveProfiles());
        LOGGER.info("App version: {}", env.getProperty("spring.application.version"));
        LOGGER.info("Application started");
    }
}
