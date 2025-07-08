package com.management.cmdb.inventory.service.entity;

import com.management.cmdb.inventory.service.entity.meta.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "link")
public class LinkEntity extends Auditable {
    @OneToOne
    private LinkTypeEntity linkType;
    @OneToOne
    private ItemEntity to;
    @OneToOne
    private ItemEntity from;
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
