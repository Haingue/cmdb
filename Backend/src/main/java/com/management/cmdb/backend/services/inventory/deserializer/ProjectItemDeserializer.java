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
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProjectItemDeserializer extends JsonDeserializer<Project> implements ContextualDeserializer {
    public static final String EnvironmentLinkType = "Has";

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        return new ProjectItemDeserializer();
    }

    @Override
    public Project deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ItemDto itemDto = deserializationContext.readValue(jsonParser, ItemDto.class);

        Map<String, String> attributes = itemDto.attributes().stream()
            .filter(attributeDto -> attributeDto.getValue() != null)
            .collect(Collectors.toMap(
                AttributeDto::getLabel,
                AttributeDto::getValue
            ));

        Set<UUID> environmentUuids = itemDto.outgoingLinks().stream()
                .filter(linkDto -> linkDto.linkType().equals(EnvironmentLinkType))
                .map(linkDto -> linkDto.targetItemId())
                .collect(Collectors.toSet());

        UUID uuid = itemDto.uuid();
        String fullName = attributes.get("fullName");
        String shortName = attributes.get("shortName");
        String description = attributes.get("description");
        BusinessService businessService = BusinessService.builder().name(attributes.get("businessService")).build();
//        UserGroup maintainers = attributes.get("maintainers");
//        UserGroup owners = attributes.get("owners");
        Set<Environment> environments = environmentUuids.stream().map(envUuid -> Environment.builder().uuid(envUuid).build()).collect(Collectors.toSet());

        Project.ProjectBuilder<?, ?> projectBuilder = Project.builder()
                .uuid(uuid)
                .fullName(fullName)
                .shortName(shortName)
                .description(description)
//                .maintainers(maintainers)
//                .owners(owners)
                .environments(environments)
                .creationDatetime(itemDto.createdDate());
        if (businessService != null) {
                projectBuilder.businessService(businessService);
        }
        return projectBuilder.build();
    }
}
