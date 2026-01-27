package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.entity.meta.Auditable;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Objects;

public class LinkEntity extends Auditable {

    @DocumentReference
    private LinkTypeEntity linkType;
    private ItemEntity sourceItem;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LinkEntity that = (LinkEntity) o;
        return Objects.equals(linkType, that.linkType) && Objects.equals(sourceItem, that.sourceItem) && Objects.equals(targetItem, that.targetItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), linkType, sourceItem, targetItem);
    }

    @Override
    public String toString() {
        return "LinkEntity{" +
                "linkType=" + (linkType != null ? linkType.getLabel() : null) +
                ", sourceItem=" + (sourceItem != null ? sourceItem.getUuid() : null) +
                ", targetItem=" + (targetItem != null ? targetItem.getUuid() : null) +
                ", description='" + description + '\'' +
                '}';
    }
}
