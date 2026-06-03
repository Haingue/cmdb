package com.management.cmdb.services.inventory.entity;

public enum PredefinedLinkType {

    IMPLEMENTED_BY("Implemented by"),
    COMMUNICATE_WITH_LINK_TYPE("Communicate with")
    ,COMPOSED_OF("Composed of")
    ,UNDEFINED_LINK_TYPE("Undefined");

    private final String label;
    private LinkTypeEntity linkTypeEntity;

    PredefinedLinkType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public LinkTypeEntity getLinkTypeEntity() {
        return linkTypeEntity;
    }

    public void setLinkTypeEntity(LinkTypeEntity linkTypeEntity) {
        this.linkTypeEntity = linkTypeEntity;
    }
}
