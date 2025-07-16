package com.management.cmdb.inventory.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableWebMvc
public class MainConfiguration {


    @Bean
    AuditorAware<UUID> auditorProvider() {
        // return () -> Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
        return () -> Optional.of(new UUID(0,0));
    }

}
