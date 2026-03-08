package com.management.cmdb.backend.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.management.cmdb.backend.services.inventory.deserializer.*;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.wrapper.PaginatedResponseDto;
import com.management.cmdb.backend.services.inventory.serializer.BusinessServiceItemSerializer;
import com.management.cmdb.backend.services.inventory.serializer.EnvironmentItemSerializer;
import com.management.cmdb.backend.services.inventory.serializer.ProjectItemSerializer;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.Hardware;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.component.Software;
import com.management.cmdb.core.models.business.component.network.Vlan;
import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.LocalDateTime;

@Configuration
public class EncoderConfiguration {

    @Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
    }

    public Module module () {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        // utiliser un object par défaut pour gérer un ItemDto
        // module.addDeserializer(ItemDto.class, new ????());
        module.addDeserializer(PaginatedResponseDto.class, new PaginatedResponseItemDeserializer());
        module.addDeserializer(BusinessService.class, new BusinessServiceItemDeserializer());
        module.addDeserializer(UserGroup.class, new UserGroupItemDeserializer());
        module.addDeserializer(Project.class, new ProjectItemDeserializer());
        module.addDeserializer(Environment.class, new EnvironmentItemDeserializer());
        module.addDeserializer(Component.class, new ComponentItemDeserializer());
        module.addDeserializer(Vlan.class, new VlanItemDeserializer());
        module.addDeserializer(Host.class, new HostDeserializer());
        module.addDeserializer(Hardware.class, new HardwareItemDeserializer());
        module.addDeserializer(Software.class, new SoftwareItemDeserializer());

        module.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        module.addSerializer(BusinessService.class, new BusinessServiceItemSerializer());
        module.addSerializer(Project.class, new ProjectItemSerializer());
        module.addSerializer(Environment.class, new EnvironmentItemSerializer());
        return module;
    }

    @Bean
    public Decoder feignDecoder(ObjectMapper jackson2ObjectMapper) {
        jackson2ObjectMapper
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(module());
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(jackson2ObjectMapper);

        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(jacksonConverter);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> httpMessageConverters;
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }

    @Bean
    public Encoder feignEncoder(ObjectMapper jackson2ObjectMapper) {
        jackson2ObjectMapper
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(module());
        HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(jackson2ObjectMapper);

        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(jacksonConverter);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> httpMessageConverters;
        return new SpringEncoder(objectFactory);
    }

}
