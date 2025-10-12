package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.entity.meta.Auditable;
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

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public String getValueStr() {
        return valueStr;
    }

    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
    }

    @Override
    public String toString() {
        return "AttributeEntity{" +
                "item=" + item.getName() +
                ", attributeType=" + attributeType.getLabel() +
                ", valueStr='" + valueStr + '\'' +
                ", createdDate=" + createdDate +
                ", createdBy=" + createdBy +
                '}';
    }
}
