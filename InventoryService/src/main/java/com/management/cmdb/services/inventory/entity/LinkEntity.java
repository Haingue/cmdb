package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.entity.meta.Auditable;
import jakarta.persistence.*;

@Entity
@Table(name = "links")
public class LinkEntity extends Auditable {
    @OneToOne
    private LinkTypeEntity linkType;
    @ManyToOne
    @JoinColumn(name = "source_item_uuid")
    private ItemEntity sourceItem;
    @ManyToOne
    @JoinColumn(name = "target_item_uuid")
    private ItemEntity targetItem;
    private String  description;

    public LinkTypeEntity getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkTypeEntity linkType) {
        this.linkType = linkType;
    }

    public ItemEntity getSourceItem() {
        return sourceItem;
    }

    public void setSourceItem(ItemEntity sourceItem) {
        this.sourceItem = sourceItem;
    }

    public ItemEntity getTargetItem() {
        return targetItem;
    }

    public void setTargetItem(ItemEntity destinationItem) {
        this.targetItem = destinationItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
