package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.entity.meta.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "item_types")
public class ItemTypeEntity extends Auditable {

    @NotBlank
    private String label;
    @NotBlank
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<AttributeTypeEntity> attributes = new HashSet<>();
//    @OneToMany(fetch = FetchType.LAZY)
//    private final Set<ItemEntity> items = new HashSet<>();

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemTypeEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), label);
    }

    @Override
    public String toString() {
        return "ItemTypeEntity{" +
                "label='" + label + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
