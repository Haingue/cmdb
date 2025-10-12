package com.management.cmdb.gateway.services.inventory.dto;

import java.util.UUID;

public record LinkDto(
        LinkTypeDto linkType,
        UUID fromItemId,
        ItemDto toItemId,
        String  description
) {
}
