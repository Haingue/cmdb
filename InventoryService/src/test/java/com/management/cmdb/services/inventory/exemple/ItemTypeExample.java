package com.management.cmdb.services.inventory.exemple;

import com.management.cmdb.services.inventory.entity.AttributeTypeEntity;
import com.management.cmdb.services.inventory.entity.ItemTypeEntity;
import com.management.cmdb.services.inventory.model.UserDetail;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public enum ItemTypeExample {

    BUSINESS_SERVICE("d2761d9d-a632-46de-bdca-8f7c35c9e038", "Business service", "Representation of a business service/process", Set.of("abbreviation"), Set.of()),
    PROJECT("385a188f-ef47-4872-9de2-c1b2ebae1ab6", "Project", "Representation of an IT project", Set.of("businessService", "fullName", "owners", "maintainers", "environments", "shortName"), Set.of()),
    ENVIRONMENT("7a70ce7e-841d-42e1-92b4-73f65a99cfba", "Environment", "Representation of a project environment", Set.of("components", "location", "status"), Set.of(AttributeTypeExample.ENV_TYPE)),
    MAINTAINER("09227a4a-74e8-4622-9aa7-9cc54c8c96c1", "Maintainer", "List of project maintainers", Set.of("email", "members"), Set.of()),
    COMPONENT("f317eae7-747a-493c-9d7f-78751f151cd9", "Component", "Representation of a project component (ex: server, application, ...)", Set.of("version"), Set.of(AttributeTypeExample.DOMAIN, AttributeTypeExample.STATUS)),
    HOST("2c7a62b4-f219-46c7-b077-e4ebd3267645", "HOST", "Representation of a host", Set.of("patchingDay", "hostname", "dns", "networkArea", "ipAddress", "macAddress", "vlan"), Set.of(AttributeTypeExample.HOST_TYPE));

    public ItemTypeEntity itemType;

    ItemTypeExample(String uuid, String label, String descriptionString, Set<String> attributes, Set<AttributeTypeExample> complexAttributes) {
        ItemTypeEntity itemTypeEntity = new ItemTypeEntity();
        itemTypeEntity.setUuid(UUID.fromString(uuid));
        itemTypeEntity.setLabel(label);
        itemTypeEntity.setDescription(descriptionString);
        itemTypeEntity.setCreatedBy(UserDetail.SYSTEM.uuid());
        itemTypeEntity.setCreatedDate(LocalDateTime.now());

        attributes.stream().map(AttributeTypeEntity::new).forEach(itemTypeEntity::addAttribute);
        complexAttributes.stream().map(attributeTypeExample -> attributeTypeExample.attributeTypeEntity)
                .forEach(itemTypeEntity::addAttribute);

        this.itemType = itemTypeEntity;
    }

    public ItemTypeEntity getItemType() {
        return itemType;
    }

    public void setItemType(ItemTypeEntity itemType) {
        this.itemType = itemType;
    }
}
