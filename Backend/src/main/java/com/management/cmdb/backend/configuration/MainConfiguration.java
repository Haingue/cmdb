package com.management.cmdb.backend.configuration;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@EnableFeignClients(basePackageClasses = InventoryServiceClient.class)
public class MainConfiguration {

    @Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
    }
}
