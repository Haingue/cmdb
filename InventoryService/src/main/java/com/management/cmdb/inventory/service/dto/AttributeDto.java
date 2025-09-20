package com.management.cmdb.inventory.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttributeDto(
    UUID uuid,
    String name,
    String value,

    LocalDateTime createdDate,
    UUID createdBy,
    UUID lastModifiedBy,
    LocalDateTime lastModifiedDate
) {}
