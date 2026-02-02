package com.management.cmdb.core.models.business.constant;

public enum ComponentType {

    HARDWARE, SOFTWARE, VIRTUAL_MACHINE, IOT, PLC, HOST;

    public static ComponentType valueOfOrDefault(String value) {
        for (ComponentType componentType : ComponentType.values()) {
            if (componentType.name().equalsIgnoreCase(value)) {
                return componentType;
            }
        }
        return HOST;
    }
}
