package com.management.cartography.core.models.business.identity;

import java.util.Set;

public record UserGroup(
        String name,
        String email,
        String description,
        UserGroup owner,
        Set<User> members
) {
}
