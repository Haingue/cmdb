package com.management.cmdb.backend.services.inventory.dto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public record ItemDto(
        UUID uuid,
        String name,
        String description,
        ItemTypeDto type,
        Set<AttributeDto> attributes,
        Set<LinkDto> outgoingLinks,
        Set<LinkDto> incomingLinks,
        LocalDateTime createdDate,
        UUID createdBy,
        UUID lastModifiedBy,
        LocalDateTime lastModifiedDate
        ) {

        public ItemDto {
                if (attributes == null) attributes = new HashSet<>();
                if (outgoingLinks == null) outgoingLinks = new HashSet<>();
                if (incomingLinks == null) incomingLinks = new HashSet<>();
        }

        public Map<String, String> getMapAttributes() {
                return attributes().stream()
                        .filter(attributeDto -> attributeDto.getValue() != null)
                        .collect(Collectors.toMap(
                                AttributeDto::getLabel,
                                AttributeDto::getValue
                        ));
        }

        @Override
        public boolean equals(Object o) {
                if (!(o instanceof ItemDto itemDto)) return false;
                return Objects.equals(uuid, itemDto.uuid)
                        && Objects.equals(name, itemDto.name)
                        && Objects.equals(description, itemDto.description)
                        && Objects.equals(type, itemDto.type)
                        && Objects.equals(createdBy, itemDto.createdBy)
                        && Objects.equals(createdDate, itemDto.createdDate);
        }

        @Override
        public int hashCode() {
                return Objects.hashCode(uuid);
        }
}
