package com.management.cmdb.services.inventory.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record AttributeDto(
    UUID uuid,
    String label,
    UUID attributeTypeId,
    String value,

    LocalDateTime createdDate,
    UUID createdBy,
    UUID lastModifiedBy,
    LocalDateTime lastModifiedDate
) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AttributeDto that)) return false;
        return Objects.equals(uuid, that.uuid) && Objects.equals(label, that.label) && Objects.equals(value, that.value) && Objects.equals(attributeTypeId, that.attributeTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, label, attributeTypeId, value);
    }
}
