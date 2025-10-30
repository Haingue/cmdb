package com.management.cmdb.services.inventory.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
