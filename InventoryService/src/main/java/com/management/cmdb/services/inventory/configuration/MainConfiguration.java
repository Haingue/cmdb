package com.management.cmdb.services.inventory.configuration;

import com.management.cmdb.services.inventory.service.ItemService;
import com.management.cmdb.services.inventory.service.ItemTypeService;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
@EnableWebMvc
@EnableScheduling
public class MainConfiguration {

    @Bean
    AuditorAware<UUID> auditorProvider() {
        // return () -> Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
        return () -> Optional.of(new UUID(0,0));
    }

    /** Use toItemId setup MCP Server tools (AI agent) **/
    @Bean
    List<ToolCallback> findTools(ItemTypeService itemTypeService, ItemService itemService) {
        return List.of(ToolCallbacks.from(itemTypeService, itemService));
    }
}
