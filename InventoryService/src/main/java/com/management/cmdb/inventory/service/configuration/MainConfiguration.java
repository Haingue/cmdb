package com.management.cmdb.inventory.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

@Configuration(proxyBeanMethods = false)
@EnableWebMvc
@EnableScheduling
public class MainConfiguration {

    @Bean
    AuditorAware<UUID> auditorProvider() {
        // return () -> Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
        return () -> Optional.of(new UUID(0,0));
    }
}
