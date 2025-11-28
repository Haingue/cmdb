package com.management.cmdb.services.aggregator.syslog.external.inventory.dto;

import java.util.UUID;

public record LinkDto(
        LinkTypeDto linkType,
        UUID sourceItemId,
        UUID targetItemId,
        String  description
) {
}
