package com.management.cmdb.inventory.service.dto;

import java.util.UUID;

public record LinkDto(
        LinkTypeDto linkType,
        UUID fromItemId,
        ItemDto to,
        String  description
) {
}
