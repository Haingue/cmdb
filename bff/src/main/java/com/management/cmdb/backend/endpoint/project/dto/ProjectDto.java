package com.management.cmdb.backend.endpoint.project.dto;

import com.management.cmdb.core.models.business.identity.UserGroup;
import com.management.cmdb.core.models.business.project.Environment;
import com.management.cmdb.core.models.technical.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ProjectDto(
        UUID uuid,
        String fullName,
        String shortName,
        String description,
        String businessServiceName,
        UserGroup maintainers,
        UserGroup owners,
        Set<Environment>environments,
        long revision,
        List<Event>events,
        LocalDateTime creationDatetime,
        LocalDateTime archiveDatetime
) {
}
