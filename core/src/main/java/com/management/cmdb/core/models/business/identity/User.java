package com.management.cmdb.core.models.business.identity;

import java.util.Set;
import java.util.UUID;

public record User(
        UUID uuid,
        String name,
        String email,
        Set<UserGroup> userGroups
) {
    public static User UNKNONW = new User(UUID.fromString("00000000-0000-0000-0000-000000000000"), "Unknown", null, Set.of());

    @Override
    public String toString() {
        return String.format("USER{uuid:%s, name:%s}", uuid, name);
    }
}
