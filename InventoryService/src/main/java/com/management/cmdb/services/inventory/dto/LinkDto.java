package com.management.cmdb.services.inventory.dto;

import java.util.UUID;

public record LinkDto(
        LinkTypeDto linkType,
        UUID sourceItemId,
        String sourceItemName,
        UUID targetItemId,
        String targetItemName,
        String  description
) {
}
