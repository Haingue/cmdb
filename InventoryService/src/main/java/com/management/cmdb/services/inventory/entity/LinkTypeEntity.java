package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.entity.meta.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "link_types")
public class LinkTypeEntity extends Auditable {
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        this.label = value;
    }

}
