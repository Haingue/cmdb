package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.entity.meta.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "link_types")
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

}
