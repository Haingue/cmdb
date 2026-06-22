package com.management.cmdb.backend.services.inventory.mapper;

import com.management.cmdb.backend.services.inventory.dto.*;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.GenericComponent;
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.business.project.Environment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemEnvironmentMapper {
    public static final String ENVIRONMENT_TYPE_LABEL = "Environment";
    public static final String COMPONENT_LINK = "Compose of";

    public Environment mapItemDtoToEnvironment(ItemDto itemDto) {
        Map<String, String> attributes = itemDto.getMapAttributes();

        // Environment attributes
        UUID uuid = itemDto.uuid();
        String status = attributes.get("Status");
        String type = attributes.get("Type");
        String location = attributes.get("Location");
        String jiraTracker = attributes.get("JiraTracker");
        String revision = attributes.get("Revision");

        Environment.EnvironmentBuilder<?, ?> environmentBuilder = Environment.builder()
                .uuid(uuid)
                .name(itemDto.name())
                .description(itemDto.description())
                .location(location)
                .jiraTracker(jiraTracker)
                .creationDatetime(itemDto.createdDate());

        if (status != null) {
            environmentBuilder.status(EnvironmentStatus.valueOf(status));
        }
        if (type != null) {
            environmentBuilder.type(EnvironmentType.valueOf(type));
        }
        if (revision != null) {
            environmentBuilder.revision(Long.parseLong(revision));
        }

        // Components
        Set<Component> components = itemDto.outgoingLinks().stream()
                .filter(linkDto -> linkDto.linkType().label().equals(COMPONENT_LINK))
                .map(linkDto -> GenericComponent.builder()
                        .uuid(linkDto.targetItemId())
                        .build())
                .collect(Collectors.toSet());
        environmentBuilder.components(components);
        return environmentBuilder.build();
    }

    public ItemDto mapEnvironmentToItemDto(Environment environment) {
        ItemTypeDto EnvironmentItemType = new ItemTypeDto(ENVIRONMENT_TYPE_LABEL);
        Set<LinkDto> outgoingLinks = new HashSet<>();

        // Components
        environment.getComponents().stream()
                .map(component -> new LinkDto(new LinkTypeDto(COMPONENT_LINK),
                        environment.getUuid(), component.getUuid(), null))
                .forEach(outgoingLinks::add);

        return new ItemDto(
                environment.getUuid(),
                environment.getName(),
                environment.getDescription(),
                EnvironmentItemType,
                Set.of(
                        AttributeDto.builder().label("Type").value(environment.getType().name()).build(),
                        AttributeDto.builder().label("Revision").value(""+environment.getRevision()).build(),
                        AttributeDto.builder().label("JiraTracker").value(environment.getJiraTracker()).build(),
                        AttributeDto.builder().label("Location").value(environment.getLocation()).build(),
                        AttributeDto.builder().label("Status").value(environment.getStatus().name()).build()
                ),
                outgoingLinks,
                Set.of(),
                environment.getCreationDatetime(),
                null,
                null,
                null
        );
    }
}
