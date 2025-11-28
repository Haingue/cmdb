package com.management.cmdb.services.aggregator.syslog.external.inventory.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttributeDto(
    UUID uuid,
    String label,
    UUID attributeTypeId,
    String value,

    LocalDateTime createdDate,
    UUID createdBy,
    UUID lastModifiedBy,
    LocalDateTime lastModifiedDate
) {}
