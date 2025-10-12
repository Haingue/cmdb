package com.management.cartography.core.models.business.technology;

import com.management.cartography.core.models.business.constants.TechnologyType;

import java.util.Optional;

public record Technology(
        String name,
        String description,
        TechnologyType type,
        Optional<String> programmingLanguage,
        Version minimalVersion,
        Version targetVersion,
        Version lastVersion
) {
    public boolean needsUpdate(Version version) {
        return 0 <= minimalVersion.compareTo(version);
    }
}
