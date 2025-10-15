package com.management.cmdb.core.models.business.identity;

public record Alias (
        String externalSystemName,
        Object externalIdentifier,

        Object internalIdentifier
) {
}
