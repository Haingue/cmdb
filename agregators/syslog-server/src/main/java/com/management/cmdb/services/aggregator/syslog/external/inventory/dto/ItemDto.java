package com.management.cmdb.services.aggregator.syslog.external.inventory.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
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

        public ItemDto {
                if (attributes == null) attributes = new HashSet<>();
                if (outgoingLinks == null) outgoingLinks = new HashSet<>();
                if (incomingLinks == null) incomingLinks = new HashSet<>();
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
