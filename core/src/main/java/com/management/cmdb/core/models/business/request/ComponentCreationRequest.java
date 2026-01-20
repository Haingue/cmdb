package com.management.cmdb.core.models.business.request;

import com.management.cmdb.core.models.business.component.Component;
import com.management.cmdb.core.models.business.identity.User;
import com.management.cmdb.core.models.business.project.Project;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class ComponentCreationRequest extends Request {

    private final UUID projectUuid;
    private final UUID environmentUuid;
    private final List<Component> components;

    public ComponentCreationRequest(UUID uuid, User requestor, Instant requestTimestamp, UUID projectUuid, UUID environmentUuid, List<Component> components) {
        super(uuid, requestor, requestTimestamp);
        this.projectUuid = projectUuid;
        this.environmentUuid = environmentUuid;
        this.components = components;
    }

    @Override
    public String toString() {
        return "ComponentCreationRequest{" +
                "uuid=" + getUuid() +
                ", requestor=" + getRequestor() +
                ", projectUuid=" + projectUuid +
                ", environmentUuid=" + environmentUuid +
                '}';
    }
}
