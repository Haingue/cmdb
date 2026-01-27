package com.management.cmdb.services.inventory.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AttributeTypeDto(
    UUID uuid,
    String label,
    String description,
    String shortDescription,
    String placeholder,
    String regex,
    Set<String>possibleValues,

    LocalDateTime createdDate,
    UUID createdBy,
    UUID lastModifiedBy,
    LocalDateTime lastModifiedDate
) {}
