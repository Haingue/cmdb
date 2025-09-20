package com.management.cmdb.inventory.service.entity;

import com.management.cmdb.inventory.service.entity.meta.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "attribute_types")
public class AttributeTypeEntity extends Auditable {

    private String label;
    private String description;
    private String shortDescription;
    private String placeholder;
    private String regex;
    private Set<String> possibleValues = new HashSet<String>();

    public AttributeTypeEntity() {
    }

    public AttributeTypeEntity(String label) {
        super();
        setUuid(UUID.randomUUID());
        this.label = label;
    }

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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Set<String> getPossibleValues() {
        return possibleValues;
    }

    public void setPossibleValues(Set<String> possibleValues) {
        this.possibleValues = possibleValues;
    }
}
