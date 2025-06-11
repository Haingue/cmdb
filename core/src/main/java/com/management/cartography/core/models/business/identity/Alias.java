package com.management.cartography.core.models.business.identity;

@Deprecated
public record Alias (
        String externalSystemName,
        Object externalIdentifier,

        Object internalIdentifier
) {
}
