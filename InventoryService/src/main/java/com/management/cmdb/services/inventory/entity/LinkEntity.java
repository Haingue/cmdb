package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.entity.meta.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "links")
public class LinkEntity extends Auditable {
    @OneToOne
    private LinkTypeEntity linkType;
    @OneToOne(orphanRemoval = true)
    private ItemEntity from;
    @OneToOne(orphanRemoval = true)
    private ItemEntity to;
    private String  description;

    public LinkTypeEntity getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkTypeEntity linkType) {
        this.linkType = linkType;
    }

    public ItemEntity getTo() {
        return to;
    }

    public void setTo(ItemEntity sourceItem) {
        this.to = sourceItem;
    }

    public ItemEntity getFrom() {
        return from;
    }

    public void setFrom(ItemEntity destinationItem) {
        this.from = destinationItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
