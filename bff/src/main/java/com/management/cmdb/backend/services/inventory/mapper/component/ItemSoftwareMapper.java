package com.management.cmdb.backend.services.inventory.mapper.component;

import com.management.cmdb.backend.services.inventory.dto.*;
import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.component.Host;
import com.management.cmdb.core.models.business.component.Software;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.TechnologyType;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemSoftwareMapper implements ItemComponentMapper<Software> {

    public static final ItemSoftwareMapper INSTANCE = new ItemSoftwareMapper();
    public static final String HOST_LINK = "Hosted on";

    private ItemSoftwareMapper() {
    }

    @Override
    public Software mapToCoreModel(ItemDto itemDto) {
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
        String softwareType = attributes.get("SoftwareType");
        String repositoryUrl = attributes.get("RepositoryUrl");

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
        if (softwareType != null) {
            component.softwareType(TechnologyType.valueOf(softwareType));
        }
        if (repositoryUrl != null) {
            component.repositoryUrl(URI.create(repositoryUrl));
        }

        // Hosts
        Set<UUID> hostUuids = itemDto.outgoingLinks().stream()
                .filter(linkDto -> linkDto.linkType().label().equals(HOST_LINK))
                .map(LinkDto::targetItemId)
                .collect(Collectors.toSet());
        component.hosts(
                hostUuids.stream()
                        .map(envUuid -> Host.builder().uuid(envUuid).build())
                        .collect(Collectors.toSet())
        );

        return component.build();
    }

    @Override
    public ItemDto mapToItemDto(Software software) {
        UUID uuid = software.getUuid();
        String name = software.getName();
        String description = software.getDescription();
        ItemTypeDto type = new ItemTypeDto(software.getType().name());

        Set<AttributeDto> attributes = new HashSet<>();

        String certificate = software.getCertificate();
        if (certificate != null) {
            attributes.add(AttributeDto.builder().label("Certificate").value(certificate).build());
        }
        Technology technology = software.getTechnology();
        if (technology != null) {
            attributes.add(AttributeDto.builder().label("Technology").value(technology.getName()).build());
        }
        Version version = software.getVersion();
        if (version != null) {
            attributes.add(AttributeDto.builder().label("Version").value(version.toString()).build());
        }
        TechnologyType softwareType = software.getSoftwareType();
        if (softwareType != null) {
            attributes.add(AttributeDto.builder().label("SoftwareType").value(softwareType.name()).build());
        }
        URI repositoryUrl = software.getRepositoryUrl();
        if (repositoryUrl != null) {
            attributes.add(AttributeDto.builder().label("RepositoryUrl").value(repositoryUrl.toString()).build());
        }

        Set<LinkDto> outgoingLinks = new HashSet<>();
        software.getHosts().stream()
                .map(host -> new LinkDto(new LinkTypeDto(HOST_LINK),
                        software.getUuid(), host.getUuid(), null))
                .forEach(outgoingLinks::add);

        LocalDateTime createdDate = software.getCreationDatetime();
        UUID createdBy = User.UNKNONW.uuid();
        LocalDateTime lastModifiedDate = software.getCreationDatetime(); //TODO add modification datetime
        UUID lastModifiedBy = User.UNKNONW.uuid();

        return new ItemDto(
                uuid,
                name,
                description,
                type,
                attributes,
                outgoingLinks,
                null,
                createdDate,
                createdBy,
                lastModifiedBy,
                lastModifiedDate
        );
    }
}
