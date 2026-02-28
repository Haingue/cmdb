package com.management.cmdb.services.inventory.dto;

import java.util.UUID;

public record LinkTypeDto(
        UUID uuid,
        String label
) {
}
