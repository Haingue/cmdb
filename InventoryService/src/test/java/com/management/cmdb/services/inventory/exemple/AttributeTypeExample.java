package com.management.cmdb.services.inventory.exemple;

import com.management.cmdb.services.inventory.entity.AttributeTypeEntity;

import java.util.Set;

public enum AttributeTypeExample {
    
    DOMAIN("Name of the active directory domain", Set.of("COMMON", "MANUFACTURING")),
    ENV_TYPE("Type of environment", Set.of("dev", "acc", "prod")),
    STATUS("Status of the component", Set.of("REQUESTED", "IN_PROGRESS", "READY", "DEPLOYED", "STOPPED", "DECOMMISSIONED")),
    HOST_TYPE("Type of the host", Set.of("UNKNOWN", "HARDWARE", "SOFTWARE", "IOT", "PLC", "PHYSICAL_MACHINE", "VIRTUAL_MACHINE")),
    ;
    
    public final AttributeTypeEntity attributeTypeEntity;

    AttributeTypeExample(String description, Set<String> setPossibleValues) {
        this.attributeTypeEntity = new AttributeTypeEntity(name());
        this.attributeTypeEntity.setDescription(description);
        this.attributeTypeEntity.setPossibleValues(setPossibleValues);
    }
}
