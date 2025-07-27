package com.management.cmdb.inventory.service.entity;

import com.management.cmdb.inventory.service.entity.meta.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "attributes")
public class AttributeEntity extends Auditable {

    @ManyToOne
    @JoinColumn
    private ItemEntity item;
    @ManyToOne
    @JoinColumn
    private AttributeTypeEntity attributeType;
    @NotBlank
    private String valueStr;

    public AttributeTypeEntity getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(AttributeTypeEntity type) {
        this.attributeType = type;
    }

}
