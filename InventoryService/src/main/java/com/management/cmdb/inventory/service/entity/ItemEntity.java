package com.management.cmdb.inventory.service.entity;

import com.management.cmdb.inventory.service.entity.meta.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "items")
public class ItemEntity extends Auditable {
    @NotBlank
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn
    private ItemTypeEntity type;

    @OneToMany(cascade = CascadeType.ALL)
    private List<LinkEntity> links = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private Set<AttributeEntity> attributes = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemTypeEntity getType() {
        return type;
    }

    public void setType(ItemTypeEntity type) {
        this.type = type;
    }

    public Set<AttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<AttributeEntity> attributes) {
        this.attributes = attributes;
    }

    public List<LinkEntity> getLinks() {
        return links;
    }

    public void setLinks(List<LinkEntity> links) {
        this.links = links;
    }
}
