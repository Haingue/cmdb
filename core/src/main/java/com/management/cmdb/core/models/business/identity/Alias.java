package com.management.cmdb.core.models.business.identity;

@Deprecated
public record Alias (
        String externalSystemName,
        Object externalIdentifier,

        Object internalIdentifier
) {
}
