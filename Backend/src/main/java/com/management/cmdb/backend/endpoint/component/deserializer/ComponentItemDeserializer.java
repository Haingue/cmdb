package com.management.cmdb.backend.endpoint.component.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.GenericComponent;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;
import com.management.cmdb.core.models.technical.Event;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ComponentItemDeserializer extends JsonDeserializer<Component> implements ContextualDeserializer {

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        return new ComponentItemDeserializer();
    }

    @Override
    public Component deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ItemDto itemDto = deserializationContext.readValue(jsonParser, ItemDto.class);

        Map<String, String> attributes = itemDto.attributes().stream().collect(Collectors.toMap(
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

        GenericComponent.GenericComponentBuilder<?, ?> component = GenericComponent.builder()
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
        return component.build();
    }
}
