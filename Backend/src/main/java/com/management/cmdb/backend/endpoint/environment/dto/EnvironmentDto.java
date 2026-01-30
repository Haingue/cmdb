package com.management.cmdb.backend.endpoint.environment.dto;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.constant.EnvironmentStatus;
import com.management.cmdb.core.models.business.constant.EnvironmentType;
import com.management.cmdb.core.models.technical.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record EnvironmentDto(
        UUID uuid,
        String location,
        EnvironmentType type,
        Set<Component>components,
        String jiraTracker,
        EnvironmentStatus status,
        long revision,
        List<Event> events,
        LocalDateTime creationDatetime,
        LocalDateTime archiveDatetime
) {
}
