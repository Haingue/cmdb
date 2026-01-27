package com.management.cmdb.services.inventory.entity;

import com.management.cmdb.services.inventory.entity.meta.Auditable;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Objects;

public class AttributeEntity extends Auditable {

    private ItemEntity item;
    @DocumentReference
    private AttributeTypeEntity attributeType;
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
    public boolean equals(Object o) {
        if (!(o instanceof AttributeEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(item, that.item) && Objects.equals(attributeType, that.attributeType) && Objects.equals(valueStr, that.valueStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), item, attributeType, valueStr);
    }

    @Override
    public String toString() {
        return "AttributeEntity{" +
                "item=" + (item != null ? item.getName() : null) +
                ", attributeType=" + (attributeType != null ? attributeType.getLabel() : null) +
                ", valueStr='" + valueStr + '\'' +
                '}';
    }
}
