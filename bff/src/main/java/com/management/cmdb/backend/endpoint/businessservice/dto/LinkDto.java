package com.management.cmdb.backend.endpoint.businessservice.dto;

import java.util.UUID;

public record LinkDto (
        UUID uuid,
        UUID projectUuid,
        UUID businessServiceUuid
) {
}