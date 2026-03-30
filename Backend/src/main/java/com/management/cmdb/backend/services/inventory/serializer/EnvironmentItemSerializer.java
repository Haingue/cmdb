package com.management.cmdb.backend.services.inventory.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.management.cmdb.backend.services.inventory.deserializer.ProjectItemDeserializer;
import com.management.cmdb.backend.services.inventory.dto.*;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EnvironmentItemSerializer extends JsonSerializer<Environment> implements ContextualSerializer {
    public static final String ITEM_TYPE_LABEL = "Environment";
    public static final String COMPONENT_LINK = "Compose of";

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        return new EnvironmentItemSerializer();
    }

    @Override
    public void serialize(Environment environment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ItemTypeDto EnvironmentItemType = new ItemTypeDto(null, ITEM_TYPE_LABEL,
                null, null, null, null, null, null);
        Set<LinkDto> outgoingLinks = new HashSet<>();
        environment.getComponents().stream()
                .map(component -> new LinkDto(new LinkTypeDto(COMPONENT_LINK),
                        environment.getUuid(), component.getUuid(), null))
                .forEach(outgoingLinks::add);

        ItemDto EnvironmentItem = new ItemDto(
                environment.getUuid(),
                environment.getName(),
                environment.getDescription(),
                EnvironmentItemType,
                Set.of(
                    new AttributeDto(null, "Type", null, environment.getType().name(), null, null, null, null),
                    new AttributeDto(null, "Revision", null, ""+environment.getRevision(), null, null, null, null),
                    new AttributeDto(null, "JiraTracker", null, environment.getJiraTracker(), null, null, null, null),
                    new AttributeDto(null, "Location", null, environment.getLocation(), null, null, null, null),
                    new AttributeDto(null, "Status", null, environment.getStatus().name(), null, null, null, null)
                ),
                Set.of(),
                outgoingLinks,
                null,
                null,
                null,
                null
        );
        jsonGenerator.writeObject(EnvironmentItem);
    }
}
