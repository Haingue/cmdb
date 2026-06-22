package com.management.cmdb.backend.services.inventory.mapper;

import com.management.cmdb.backend.services.inventory.dto.AttributeDto;
import com.management.cmdb.backend.services.inventory.dto.ItemDto;
import com.management.cmdb.backend.services.inventory.dto.ItemTypeDto;
import com.management.cmdb.core.models.business.component.GenericComponent;
import com.management.cmdb.core.models.business.constant.ComponentType;
import com.management.cmdb.core.models.business.constant.TechnologyType;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.technology.Technology;
import com.management.cmdb.core.models.business.technology.Version;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ItemGenericComponentMapper {

    public static GenericComponent mapItemDtoToGenericComponent(ItemDto itemDto) {
        // Component attributes
        GenericComponent.GenericComponentBuilder<?, ?> genericComponentBuilder = GenericComponent.builder()
                .uuid(itemDto.uuid())
                .name(itemDto.name())
                .description(itemDto.description())
                .type(ComponentType.valueOf(itemDto.type().label()))
                .creationDatetime(itemDto.createdDate());

        Map<String, String> mapAttributes = itemDto.getMapAttributes();
        String certificate = mapAttributes.get("Certificate");
        String technology = mapAttributes.get("Technology");
        String version = mapAttributes.get("Version");

        if (certificate != null) {
            genericComponentBuilder.certificate(certificate);
        }
        if (technology != null) {
            genericComponentBuilder.technology(
                    Technology.builder()
                            .name(technology)
                            .build());
        }
        if (version != null) {
            genericComponentBuilder.version(Version.fromString(version));
        }

        return genericComponentBuilder.build();
    }

    public static ItemDto mapGenericComponentToItemDto(GenericComponent component) {
        UUID uuid = component.getUuid();
        String name = component.getName();
        String description = component.getDescription();
        ItemTypeDto type = new ItemTypeDto(component.getType().name());

        Set<AttributeDto> attributes = new HashSet<>();

        String certificate = component.getCertificate();
        if (certificate != null) {
            attributes.add(AttributeDto.builder().label("Certificate").value(certificate).build());
        }
        Technology technology = component.getTechnology();
        if (technology != null) {
            attributes.add(AttributeDto.builder().label("Technology").value(technology.getName()).build());
        }
        Version version = component.getVersion();
        if (version != null) {
            attributes.add(AttributeDto.builder().label("Version").value(version.toString()).build());
        }

        LocalDateTime createdDate = component.getCreationDatetime();
        UUID createdBy = User.UNKNONW.uuid();
        LocalDateTime lastModifiedDate = component.getCreationDatetime(); //TODO add modification datetime
        UUID lastModifiedBy = User.UNKNONW.uuid();

        return new ItemDto(
                uuid,
                name,
                description,
                type,
                attributes,
                null,
                null,
                createdDate,
                createdBy,
                lastModifiedBy,
                lastModifiedDate
        );
    }

}
