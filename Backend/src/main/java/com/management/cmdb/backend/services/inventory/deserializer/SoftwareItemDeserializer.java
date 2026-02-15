package com.management.cmdb.backend.services.inventory.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.component.Software;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class SoftwareItemDeserializer extends JsonDeserializer<Software> implements ContextualDeserializer {

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        return new SoftwareItemDeserializer();
    }

    @Override
    public Software deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ItemDto itemDto = deserializationContext.readValue(jsonParser, ItemDto.class);

        Map<String, String> attributes = itemDto.attributes().stream()
            .filter(attributeDto -> attributeDto.getValue() != null)
            .collect(Collectors.toMap(
                AttributeDto::getLabel,
                AttributeDto::getValue
            ));
        ComponentType componentType = ComponentType.valueOfOrDefault(itemDto.type().label());
        UUID uuid = itemDto.uuid();
        String name = itemDto.name();
        String description = itemDto.description();
        String certificate = attributes.get("Certificate");
        String version = attributes.get("Version");
        String technology = attributes.get("Technology");
        String host = attributes.get("Host");

        Software.SoftwareBuilder<?, ?> component = Software.builder()
                .uuid(uuid)
                .name(name)
                .description(description)
                .type(componentType)
                .certificate(certificate)
                .creationDatetime(itemDto.createdDate());
        if (version != null) {
            component.version(Version.fromString(version));
        }
        if (technology != null) {
            component.technology(Technology.builder().name(technology).build());
        }
        if (host != null) {
            component.host(Host.builder().name(host).build());
        }
        return component.build();
    }
}
