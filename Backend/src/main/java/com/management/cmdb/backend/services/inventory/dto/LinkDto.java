package com.management.cmdb.backend.services.inventory.dto;

import java.util.UUID;

public record LinkDto(
        LinkTypeDto linkType,
        UUID fromItemId,
        ItemDto toItemId,
        String  description
) {
}
