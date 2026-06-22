package com.management.cmdb.backend.services.inventory.mapper;

import com.management.cmdb.backend.services.inventory.deserializer.ProjectItemDeserializer;
import com.management.cmdb.backend.services.inventory.dto.*;
import com.management.cmdb.core.models.business.project.BusinessService;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.project.Project;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemProjectMapper {
    private static final String PROJECT_TYPE_LABEL = "Project";
    private static final String LINK_TYPE_BUSINESS_SERVICE = "Implements";
    private static final String LINK_TYPE_ENVIRONMENT = "Deployed in";

    Project mapItemDtoToCoreModel(ItemDto itemDto) {
        Map<String, String> attributes = itemDto.attributes().stream()
            .filter(attributeDto -> attributeDto.getValue() != null)
            .collect(Collectors.toMap(
                    AttributeDto::getLabel,
                    AttributeDto::getValue
            ));

        // Project attributes
        UUID uuid = itemDto.uuid();
        String fullName = attributes.get("FullName");
        String shortName = attributes.get("ShortName");
        String description = attributes.get("Description");
//        UserGroup maintainers = attributes.get("maintainers");
//        UserGroup owners = attributes.get("owners");

        Project.ProjectBuilder<?, ?> projectBuilder = Project.builder()
                .uuid(uuid)
                .fullName(fullName)
                .shortName(shortName)
                .description(description)
//                .maintainers(maintainers)
//                .owners(owners)
                .creationDatetime(itemDto.createdDate());

        // Business Service
        itemDto.outgoingLinks().stream()
            .filter(linkDto -> linkDto.linkType().label().equals(LINK_TYPE_BUSINESS_SERVICE))
            .map(linkDto -> BusinessService.builder()
                    .name(linkDto.targetItemId().toString())
                    .build())
            .findFirst()
            .ifPresent(projectBuilder::businessService);

        // Environment
        Set<UUID> environmentUuids = itemDto.outgoingLinks().stream()
                .filter(linkDto -> linkDto.linkType().label().equals(LINK_TYPE_ENVIRONMENT))
                .map(LinkDto::targetItemId)
                .collect(Collectors.toSet());
        projectBuilder.environments(
                environmentUuids.stream()
                        .map(envUuid -> Environment.builder().uuid(envUuid).build())
                        .collect(Collectors.toSet())
        );

        return projectBuilder.build();
    }

    public ItemDto mapCoreModelToDto(Project project) {
        ItemTypeDto projectItemType = new ItemTypeDto(null, PROJECT_TYPE_LABEL,
                null, null, null, null, null, null);
//        String ownerUserGroups = null;
//        if (project.getOwners() != null) {
//            ownerUserGroups = project.getOwners().name();
//        }
//        String maintainerUserGroups = null;
//        if (project.getMaintainers() != null) {
//            maintainerUserGroups = project.getMaintainers().name();
//        }
        Set<LinkDto> outgoingLinks = new HashSet<>();
        project.getEnvironments().stream()
                .map(environment -> new LinkDto(new LinkTypeDto(ProjectItemDeserializer.EnvironmentLinkType),
                        project.getUuid(), environment.getUuid(), null))
                .forEach(outgoingLinks::add);

        return new ItemDto(
                null,
                project.getFullName(),
                project.getDescription(),
                projectItemType,
                Set.of(
                        new AttributeDto(null, "FullName", null, project.getFullName(), null, null, null, null),
                        new AttributeDto(null, "ShortName", null, project.getShortName(), null, null, null, null),
                        new AttributeDto(null, "BusinessService", null, project.getBusinessService().getName(), null, null, null, null)
//                        new AttributeDto(null, "Owners", null, ownerUserGroups, null, null, null, null),
//                        new AttributeDto(null, "Maintainers", null, maintainerUserGroups, null, null, null, null)
                ),
                Set.of(),
                outgoingLinks,
                project.getCreationDatetime(),
                null,
                null,
                null
        );
    }



}
