package com.management.cmdb.core.models.business.identity;

import java.util.UUID;

public record User(
        UUID uuid,
        String name,
        String email
) {
}
