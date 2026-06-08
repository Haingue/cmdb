package com.management.cmdb.backend.services.inventory.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.management.cmdb.backend.services.inventory.deserializer.ProjectItemDeserializer;
import com.management.cmdb.backend.services.inventory.dto.*;
import com.management.cmdb.core.models.business.project.Project;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ProjectItemSerializer extends JsonSerializer<Project> implements ContextualSerializer {
    public static final String PROJECT = "Project";

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        return new ProjectItemSerializer();
    }

    @Override
    public void serialize(Project project, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ItemTypeDto projectItemType = new ItemTypeDto(null, PROJECT,
                null, null, null, null, null, null);
        String ownerUserGroups = null;
        if (project.getOwners() != null) {
            ownerUserGroups = project.getOwners().name();
        }
        String maintainerUserGroups = null;
        if (project.getMaintainers() != null) {
            maintainerUserGroups = project.getMaintainers().name();
        }
        Set<LinkDto> outgoingLinks = new HashSet<>();
        project.getEnvironments().stream()
                .map(environment -> new LinkDto(new LinkTypeDto(ProjectItemDeserializer.EnvironmentLinkType),
                        project.getUuid(), environment.getUuid(), null))
                .forEach(outgoingLinks::add);

        ItemDto projectItem = new ItemDto(
                null,
                project.getFullName(),
                project.getDescription(),
                projectItemType,
                Set.of(
                    new AttributeDto(null, "FullName", null, project.getFullName(), null, null, null, null),
                    new AttributeDto(null, "ShortName", null, project.getShortName(), null, null, null, null),
                    new AttributeDto(null, "BusinessService", null, project.getBusinessService().getName(), null, null, null, null),
                    new AttributeDto(null, "Owners", null, ownerUserGroups, null, null, null, null),
                    new AttributeDto(null, "Maintainers", null, maintainerUserGroups, null, null, null, null)
                ),
                Set.of(),
                outgoingLinks,
                null,
                null,
                null,
                null
        );
        jsonGenerator.writeObject(projectItem);
    }
}
