package com.management.cmdb.services.aggregator.syslog.configuration;

import com.management.cmdb.services.aggregator.syslog.external.inventory.InventoryServiceClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableConfigurationProperties
@EnableScheduling
@EnableFeignClients(basePackageClasses = InventoryServiceClient.class)
public class MainConfiguration {
}
