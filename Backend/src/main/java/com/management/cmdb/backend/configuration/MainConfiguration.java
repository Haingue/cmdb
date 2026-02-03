package com.management.cmdb.backend.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.management.cmdb.backend.endpoint.component.deserializer.*;
import com.management.cmdb.backend.services.inventory.InventoryServiceClient;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.Hardware;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.component.Software;
import com.management.cmdb.core.models.business.component.network.Vlan;
import com.management.cmdb.core.models.business.project.Environment;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.Decoder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
    }

    @Bean
    public Module module () {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Vlan.class, new VlanItemDeserializer());
        module.addDeserializer(Environment.class, new EnvironmentItemDeserializer());
        module.addDeserializer(Component.class, new ComponentItemDeserializer());
        module.addDeserializer(Host.class, new HostDeserializer());
        module.addDeserializer(Hardware.class, new HardwareItemDeserializer());
        module.addDeserializer(Software.class, new SoftwareItemDeserializer());
        return module;
    }

//
//    @Bean
//    public Decoder feignDecoder() {
//        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
//
//        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(jacksonConverter);
//        ObjectFactory<HttpMessageConverters> objectFactory = () -> httpMessageConverters;
//
//
//        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
//    }


    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOriginPattern(allowedHost);
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedHeader("*");

        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(Duration.ofMinutes(60));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
