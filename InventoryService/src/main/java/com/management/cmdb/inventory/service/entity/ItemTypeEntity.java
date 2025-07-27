package com.management.cmdb.inventory.service.entity;

import com.management.cmdb.inventory.service.entity.meta.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "item_types")
public class ItemTypeEntity extends Auditable {

    @NotBlank
    private String label;
    @NotBlank
    private String description;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<AttributeTypeEntity> attributes = new HashSet<AttributeTypeEntity>();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<AttributeTypeEntity> getAttributes() {
        return attributes;
    }

    public boolean addAttribute(AttributeTypeEntity attributeTypeEntity) {
        return this.attributes.add(attributeTypeEntity);
    }

    public void setAttributes(Set<AttributeTypeEntity> attributes) {
        this.attributes = attributes;
    }
}
