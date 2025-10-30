package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.entity.meta.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<AttributeEntity> attributes = new HashSet<>();

    @OneToMany(mappedBy = "sourceItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<LinkEntity> outgoingLinks = new HashSet<>();
    @OneToMany(mappedBy = "targetItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<LinkEntity> incomingLinks = new HashSet<>();

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

    public Set<LinkEntity> getOutgoingLinks() {
        return outgoingLinks;
    }

    public void addToLinks(Set<LinkEntity> toLinks) {
        this.outgoingLinks.addAll(toLinks);
    }

    public Set<LinkEntity> getIncomingLinks() {
        return incomingLinks;
    }

    public void addFromLinks(Set<LinkEntity> fromLinks) {
        this.incomingLinks.addAll(fromLinks);
    }

    public Set<AttributeEntity> getAttributes() {
        return attributes;
    }

}
