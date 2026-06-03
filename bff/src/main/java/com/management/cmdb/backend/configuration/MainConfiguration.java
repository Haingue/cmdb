package com.management.cmdb.backend.configuration;

import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.time.Duration;

@Configuration
@EnableFeignClients(basePackageClasses = InventoryServiceClient.class)
public class MainConfiguration {

    @Value("${ALLOWED_HOST_REGEX:*}")
    private String allowedHost;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOriginPattern(allowedHost);
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedHeader("*");

        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(Duration.ofMinutes(60));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
