package com.management.cmdb.inventory.service.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record ItemTypeDto(
        UUID uuid,
        String label,
        String description,
        Set<AttributeTypeDto> attributes,

        LocalDateTime createdDate,
        UUID createdBy,
        UUID lastModifiedBy,
        LocalDateTime lastModifiedDate
) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemTypeDto dto)) return false;
        return Objects.equals(uuid, dto.uuid)
                && Objects.equals(label, dto.label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
