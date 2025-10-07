package com.management.cmdb.services.inventory.model.itemTypes;

import com.management.cmdb.services.inventory.entity.AttributeTypeEntity;
import com.management.cmdb.services.inventory.entity.ItemTypeEntity;
import com.management.cmdb.services.inventory.model.UserDetail;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public enum DefaultItemType {

    //BUSINESS_SERVICE(), PROJECT(), ENVIRONMENT(), MAINTAINER(), COMPONENT(),
    HOST();

    public final ItemTypeEntity itemType;

    DefaultItemType() {
        ItemTypeEntity itemTypeEntity = new ItemTypeEntity();
        itemTypeEntity.setUuid(UUID.randomUUID());
        itemTypeEntity.setCreatedBy(UserDetail.SYSTEM.uuid());
        itemTypeEntity.setCreatedDate(LocalDateTime.now());

        switch (this.name()) {
            case "BUSINESS_SERVICE":
                itemTypeEntity.setLabel("Business service");
                itemTypeEntity.setDescription("Representation of a business service/process");

                itemTypeEntity.addAttribute(new AttributeTypeEntity("abbreviation"));
                break;
            case "PROJECT":
                itemTypeEntity.setLabel("Project");
                itemTypeEntity.setDescription("Representation of an IT project");

                itemTypeEntity.addAttribute(new AttributeTypeEntity("description"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("businessService"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("fullName"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("owners"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("maintainers"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("environments"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("shortName"));
                break;
            case "ENVIRONMENT":
                itemTypeEntity.setLabel("Environment");
                itemTypeEntity.setDescription("Representation of a project environment");

                AttributeTypeEntity environmentType = new AttributeTypeEntity("type");
                environmentType.setPossibleValues(Set.of("dev", "acc", "prod"));
                itemTypeEntity.addAttribute(environmentType);
                AttributeTypeEntity environmentStatus = new AttributeTypeEntity("status");
                environmentStatus.setPossibleValues(Set.of("REQUESTED", "IN_PROGRESS", "READY", "DEPLOYED", "STOPPED", "DECOMMISSIONED"));
                itemTypeEntity.addAttribute(environmentStatus);
                itemTypeEntity.addAttribute(new AttributeTypeEntity("components"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("location"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("status"));
                break;
            case "MAINTAINER":
                itemTypeEntity.setLabel("Maintainer");
                itemTypeEntity.setDescription("List of project maintainers");

                itemTypeEntity.addAttribute(new AttributeTypeEntity("email"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("members"));
                break;
            case "COMPONENT":
                itemTypeEntity.setLabel("Component");
                itemTypeEntity.setDescription("Representation of a project component (ex: server, application, ...)");

                itemTypeEntity.addAttribute(new AttributeTypeEntity("version"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("description"));
                AttributeTypeEntity componentTypes = new AttributeTypeEntity("type");
                componentTypes.setPossibleValues(Set.of("UNKNOWN", "HARDWARE", "SOFTWARE", "IOT", "PLC", "PHYSICAL_MACHINE", "VIRTUAL_MACHINE"));
                itemTypeEntity.addAttribute(componentTypes);
                break;
            case "HOST":
                itemTypeEntity.setLabel("HOST");
                itemTypeEntity.setDescription("Representation of a host");

                itemTypeEntity.addAttribute(new AttributeTypeEntity("patchingDay"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("hostname"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("dns"));
                AttributeTypeEntity domain = new AttributeTypeEntity("domain");
                domain.setPossibleValues(Set.of("COMMON", "MANUFACTURING"));
                itemTypeEntity.addAttribute(domain);
                itemTypeEntity.addAttribute(new AttributeTypeEntity("networkArea"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("ipAddress"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("macAddress"));
                itemTypeEntity.addAttribute(new AttributeTypeEntity("vlan"));
                break;
            default:
                throw new RuntimeException("Invalid item label");
        }
        this.itemType = itemTypeEntity;
    }

}
