package com.management.cmdb.services.inventory.model;

import java.util.UUID;

public record UserDetail(UUID uuid, String firstname, String email) {

    public static UserDetail SYSTEM = new UserDetail(new UUID(0, 0), "System", "System");
    public static UserDetail UNKNOWN = new UserDetail(new UUID(0, 1), "Unknown", "Unknown");

}
