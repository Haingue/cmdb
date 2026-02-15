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
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.business.project.Environment;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class EnvironmentItemDeserializer extends JsonDeserializer<Environment> implements ContextualDeserializer {

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        return new EnvironmentItemDeserializer();
    }

    @Override
    public Environment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ItemDto itemDto = deserializationContext.readValue(jsonParser, ItemDto.class);

        Map<String, String> attributes = itemDto.attributes().stream()
            .filter(attributeDto -> attributeDto.getValue() != null)
            .collect(Collectors.toMap(
                AttributeDto::getLabel,
                AttributeDto::getValue
            ));

        UUID uuid = itemDto.uuid();
        String status = attributes.get("status");
        String type = attributes.get("type");
        String location = attributes.get("Location");
        String jiraTracker = attributes.get("JiraTracker");

        Environment.EnvironmentBuilder<?, ?> environmentBuilder = Environment.builder()
                .uuid(uuid)
                .location(location)
                .jiraTracker(jiraTracker)
                .creationDatetime(itemDto.createdDate());
        if (status != null) {
            environmentBuilder.status(EnvironmentStatus.valueOf(status));
        }
        if (type != null) {
            environmentBuilder.type(EnvironmentType.valueOf(type));
        }
        return environmentBuilder.build();
    }
}
