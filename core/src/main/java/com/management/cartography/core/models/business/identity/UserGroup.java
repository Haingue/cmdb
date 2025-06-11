package com.management.cartography.core.models.business.identity;

import java.util.Set;

public record UserGroup(
        String name,
        String email,
        Set<User> members
) {
}
