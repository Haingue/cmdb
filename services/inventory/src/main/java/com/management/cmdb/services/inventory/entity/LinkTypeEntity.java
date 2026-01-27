package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.entity.meta.Auditable;

import java.util.Objects;
import java.util.UUID;

public class LinkTypeEntity extends Auditable {

    private String label;

    public LinkTypeEntity() {
        super();
    }

    public LinkTypeEntity(String label) {
        this.label = label;
        this.setUuid(UUID.randomUUID());
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        this.label = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LinkTypeEntity that = (LinkTypeEntity) o;
        return Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), label);
    }

    @Override
    public String toString() {
        return "LinkTypeEntity{" +
                "label='" + label + '\'' +
                '}';
    }
}
