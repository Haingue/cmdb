package com.management.cmdb.inventory.service.entity;

import com.management.cmdb.inventory.service.entity.meta.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "link_type")
public class LinkTypeEntity extends Auditable {
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        this.label = value;
    }

}
