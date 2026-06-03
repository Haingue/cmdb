package com.management.cmdb.backend.services.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AttributeDto {

    private UUID uuid;
    private String label;
    private UUID attributeTypeId;
    private String value;
    private LocalDateTime createdDate;
    private UUID createdBy;
    private UUID lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AttributeDto) obj;
        return Objects.equals(this.uuid, that.uuid) &&
                Objects.equals(this.label, that.label) &&
                Objects.equals(this.attributeTypeId, that.attributeTypeId) &&
                Objects.equals(this.value, that.value) &&
                Objects.equals(this.createdDate, that.createdDate) &&
                Objects.equals(this.createdBy, that.createdBy) &&
                Objects.equals(this.lastModifiedBy, that.lastModifiedBy) &&
                Objects.equals(this.lastModifiedDate, that.lastModifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, label, attributeTypeId, value, createdDate, createdBy, lastModifiedBy, lastModifiedDate);
    }

    @Override
    public String toString() {
        return "AttributeDto[" +
                "uuid=" + uuid + ", " +
                "label=" + label + ", " +
                "attributeTypeId=" + attributeTypeId + ", " +
                "value=" + value + ", " +
                "createdDate=" + createdDate + ", " +
                "createdBy=" + createdBy + ", " +
                "lastModifiedBy=" + lastModifiedBy + ", " +
                "lastModifiedDate=" + lastModifiedDate + ']';
    }
}
